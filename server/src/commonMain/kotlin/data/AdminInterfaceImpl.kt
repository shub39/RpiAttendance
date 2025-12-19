package data

import AdminInterface
import EnrollState
import Result
import data.database.AttendanceLogDao
import data.database.CourseDao
import data.database.StudentDao
import data.database.TeacherDao
import domain.SensorServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

class AdminInterfaceImpl(
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val courseDao: CourseDao,
    private val attendanceLogDao: AttendanceLogDao,
    private val sensorServer: SensorServer
) : AdminInterface {
    val scope = CoroutineScope(Dispatchers.Default)

    override fun getStudents(): Flow<List<Student>> = studentDao
        .getAllStudents()
        .map { flow -> flow.map { student -> student.toStudent() } }

    override fun getTeachers(): Flow<List<Teacher>> = teacherDao
        .getAllTeachers()
        .map { flow -> flow.map { teachers -> teachers.toTeacher() } }

    override fun getCourses(): Flow<List<Course>> = courseDao
        .getAllCourses()
        .map { flow -> flow.map { course -> course.toCourse() } }

    override fun getAttendanceLogs(): Flow<List<AttendanceLog>> = attendanceLogDao
        .getAttendanceLogs()
        .map { flow -> flow.map { attendanceLog -> attendanceLog.toAttendanceLog() } }

    override suspend fun upsertStudent(student: Student) {
        studentDao.upsert(student.toStudentEntity())
    }

    override suspend fun upsertTeacher(teacher: Teacher) {
        teacherDao.upsert(teacher.toTeacherEntity())
    }

    override suspend fun upsertCourse(course: Course) {
        courseDao.upsert(course.toCourseEntity())
    }

    override suspend fun deleteStudent(student: Student) {
        student.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        studentDao.delete(student.toStudentEntity())
    }

    override suspend fun deleteTeacher(teacher: Teacher) {
        teacher.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
        teacherDao.delete(teacher.toTeacherEntity())
    }

    override suspend fun deleteCourse(course: Course) {
        studentDao
            .getAllStudents()
            .first()
            .filter { it.courseId == course.id }
            .forEach { studentEntity ->
                studentEntity.biometricId?.toIntOrNull()?.let { deleteBiometrics(it) }
            }
        courseDao.delete(course.toCourseEntity())
    }

    override suspend fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState> =
        addBiometricDetails { biometricId ->
            scope.launch {
                studentDao.upsert(
                    student.copy(
                        biometricId = biometricId
                    ).toStudentEntity()
                )
            }
        }

    override suspend fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState> =
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
                        emit(EnrollState.FaceEnrolled)
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