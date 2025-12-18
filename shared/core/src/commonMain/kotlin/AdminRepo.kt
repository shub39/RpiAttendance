import kotlinx.coroutines.flow.Flow
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

// for admins, manage everything in the db
interface AdminRepo {
    fun getStudents(): Flow<List<Student>>
    fun getTeachers(): Flow<List<Teacher>>
    fun getCourses(): Flow<List<Course>>
    fun getAttendanceLogs(): Flow<List<AttendanceLog>>

    suspend fun upsertAttendanceLog(attendanceLog: AttendanceLog)
    suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog)

    suspend fun upsertStudent(student: Student)
    suspend fun deleteStudent(student: Student)

    suspend fun upsertTeacher(teacher: Teacher)
    suspend fun deleteTeacher(teacher: Teacher)

    suspend fun upsertCourse(course: Course)
    suspend fun deleteCourse(course: Course)
}