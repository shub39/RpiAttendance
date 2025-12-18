package domain

import kotlinx.coroutines.flow.Flow
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

// for the server tasks, only interacts with attendancelogs
interface AttendanceRepo {
    fun getStudents(): Flow<List<Student>>
    fun getTeachers(): Flow<List<Teacher>>
    fun getCourses(): Flow<List<Course>>
    fun getAttendanceLogs(): Flow<List<AttendanceLog>>

    suspend fun upsertAttendanceLog(attendanceLog: AttendanceLog)
    suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog)
}