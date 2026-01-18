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
import kotlinx.datetime.toLocalDateTime
import models.EntityType
import models.Session
import models.Student
import models.Teacher

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

    override suspend fun getSessionsForDate(courseId: Long, date: LocalDate): List<Session> {
        val logs = attendanceLogDao.getAttendanceLogs().first()
            .filter { it.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).date == date }

        val teachers = teacherDao.getAllTeachers().first()
        val students = studentDao.getAllStudents().first()

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

                val studentsPresent = students
                    .filter { student ->
                        logs.any { log ->
                            log.entityId == student.id &&
                                    log.timeStamp >= sessionStartTime &&
                                    log.timeStamp <= sessionEndTime
                        }
                    }
                    .map { it.toStudent() }

                Session(
                    teacher = teacher.toTeacher(),
                    startTime = sessionStartTime.toLocalDateTime(TimeZone.currentSystemDefault()).time,
                    endTime = sessionEndTime.toLocalDateTime(TimeZone.currentSystemDefault()).time,
                    totalStudents = students.size,
                    students = studentsPresent
                )
            }
    }

    override suspend fun upsertStudent(student: Student) {
        studentDao.upsert(student.toStudentEntity())
    }

    override suspend fun upsertTeacher(teacher: Teacher) {
        teacherDao.upsert(teacher.toTeacherEntity())
    }

    override suspend fun deleteStudent(student: Student) {
        student.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        studentDao.delete(student.toStudentEntity())
    }

    override suspend fun deleteTeacher(teacher: Teacher) {
        teacher.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        teacherDao.delete(teacher.toTeacherEntity())
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
        sensorServer.deleteFingerPrint(id)
        sensorServer.deleteFace(id.toString())
    }
}