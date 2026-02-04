package data

import AdminInterface
import EnrollState
import Result
import data.database.AttendanceLogDao
import data.database.StudentDao
import data.database.TeacherDao
import domain.SensorServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import logInfo
import models.AttendanceLog
import models.DetailedAttendanceLog
import models.EntityType
import models.Session
import models.Student
import models.Teacher
import kotlin.time.Duration.Companion.days

class AdminInterfaceImpl(
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val attendanceLogDao: AttendanceLogDao,
    private val sensorServer: SensorServer
) : AdminInterface {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override suspend fun getStatus(): Boolean = true

    override fun getStudents(): Flow<List<Student>> = studentDao
        .getAllStudents()
        .map { flow -> flow.map { student -> student.toStudent() } }
        .flowOn(Dispatchers.IO)

    override fun getTeachers(): Flow<List<Teacher>> = teacherDao
        .getAllTeachers()
        .map { flow -> flow.map { teachers -> teachers.toTeacher() } }
        .flowOn(Dispatchers.IO)

    override fun getDetailedAttendanceLogs(): Flow<List<DetailedAttendanceLog>> = attendanceLogDao
        .getAttendanceLogs()
        .map { flow ->
            flow.mapNotNull { log ->
                when (log.entityType) {
                    EntityType.STUDENT -> {
                        studentDao.getStudentById(log.entityId)?.let { studentEntity ->
                            DetailedAttendanceLog.StudentLog(
                                student = studentEntity.toStudent(),
                                log = log.toAttendanceLog()
                            )
                        }
                    }

                    EntityType.TEACHER -> {
                        teacherDao.getTeacherById(log.entityId)?.let { teacherEntity ->
                            DetailedAttendanceLog.TeacherLog(
                                teacher = teacherEntity.toTeacher(),
                                log = log.toAttendanceLog()
                            )
                        }
                    }
                }
            }
        }
        .flowOn(Dispatchers.IO)

    override suspend fun getSessionsForDate(date: LocalDate): List<Session> {
        val startTime = date.atStartOfDayIn(TimeZone.currentSystemDefault())
        val endTime = startTime + 1.days

        val logs = attendanceLogDao.getLogsBetween(startTime, endTime)
        val teachers = teacherDao.getTeachersByIds(logs.map { it.entityId }.distinct()).map { it.toTeacher() }
        val students = studentDao.getAllStudents().first().map { it.toStudent() }

        return logs
            .filter { it.entityType == EntityType.TEACHER }
            .groupBy { it.entityId }
            .mapNotNull { (teacherId, teacherLogs) ->
                val teacher = teachers.find { it.id == teacherId } ?: return@mapNotNull null

                val sessionStartTime = teacherLogs.minByOrNull { it.timeStamp }?.timeStamp
                val sessionEndTime = teacherLogs.maxByOrNull { it.timeStamp }?.timeStamp

                if (sessionStartTime == null || sessionEndTime == null) {
                    return@mapNotNull null
                }

                val studentsPresent = studentDao.getStudentsByIds(
                    logs.filter {
                        it.entityType == EntityType.STUDENT &&
                                it.timeStamp >= sessionStartTime &&
                                it.timeStamp <= sessionEndTime
                    }.map { it.entityId }.distinct()
                ).map { it.toStudent() }


                Session(
                    teacher = teacher,
                    startTime = sessionStartTime.toLocalDateTime(TimeZone.currentSystemDefault()).time,
                    endTime = sessionEndTime.toLocalDateTime(TimeZone.currentSystemDefault()).time,
                    totalStudents = students.size,
                    students = studentsPresent
                )
            }
    }

    override suspend fun upsertStudent(student: Student) {
        logInfo("upserting student $student")
        val presentStudent = studentDao.getStudentById(student.id)
        if (presentStudent != null) {
            if (presentStudent.biometricId != null && student.biometricId == null) {
                presentStudent.biometricId.toIntOrNull()?.let { deleteBiometrics(it) }
            }
        }
        studentDao.upsert(student.toStudentEntity())
    }

    override suspend fun upsertTeacher(teacher: Teacher) {
        logInfo("upserting teacher $teacher")
        val presentTeacher = teacherDao.getTeacherById(teacher.id)
        if (presentTeacher != null) {
            if (presentTeacher.biometricId != null && teacher.biometricId == null) {
                presentTeacher.biometricId.toIntOrNull()?.let { deleteBiometrics(it) }
            }
        }
        teacherDao.upsert(teacher.toTeacherEntity())
    }

    override suspend fun deleteStudent(student: Student) {
        logInfo("deleting student $student")
        student.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        studentDao.delete(student.toStudentEntity())
    }

    override suspend fun deleteTeacher(teacher: Teacher) {
        logInfo("deleting teacher $teacher")
        teacher.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        teacherDao.delete(teacher.toTeacherEntity())
    }

    override suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog) {
        logInfo("deleting attendance log $attendanceLog")
        attendanceLogDao.delete(attendanceLog.toAttendanceLogEntity())
    }

    override fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState> =
        addBiometricDetails { biometricId ->
            scope.launch {
                studentDao.upsert(
                    student.copy(
                        biometricId = biometricId
                    ).toStudentEntity()
                )
            }
        }

    override fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState> =
        addBiometricDetails { biometricId ->
            scope.launch {
                teacherDao.upsert(
                    teacher.copy(
                        biometricId = biometricId
                    ).toTeacherEntity()
                )
            }
        }

    private fun addBiometricDetails(
        onSuccess: (String) -> Unit
    ): Flow<EnrollState> = flow {
        emit(EnrollState.Enrolling)

        when (val fingerprintResult = sensorServer.enrollFingerPrint()) {
            is Result.Error -> emit(EnrollState.EnrollFailed(fingerprintResult.debugMessage))
            is Result.Success -> {
                emit(EnrollState.FingerprintEnrolled)

                when (val faceResult = sensorServer.enrollFace(fingerprintResult.data.toString())) {
                    is Result.Error -> {
                        sensorServer.deleteFingerPrint(fingerprintResult.data)
                        emit(EnrollState.EnrollFailed(faceResult.debugMessage))
                    }

                    is Result.Success -> {
                        emit(EnrollState.EnrollComplete)
                        onSuccess(fingerprintResult.data.toString())
                    }
                }
            }
        }
    }

    private suspend fun deleteBiometrics(id: Int) {
        logInfo("deleting biometrics for $id")
        sensorServer.deleteFingerPrint(id)
        sensorServer.deleteFace(id.toString())
    }
}