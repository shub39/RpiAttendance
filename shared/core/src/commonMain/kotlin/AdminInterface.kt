import kotlinx.coroutines.flow.Flow
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

// what the admin can do
interface AdminInterface {
    fun getStudents(): Flow<List<Student>>
    fun getTeachers(): Flow<List<Teacher>>
    fun getCourses(): Flow<List<Course>>
    fun getAttendanceLogs(): Flow<List<AttendanceLog>>

    suspend fun upsertStudent(student: Student)
    suspend fun upsertTeacher(teacher: Teacher)
    suspend fun upsertCourse(course: Course)

    suspend fun deleteStudent(student: Student)
    suspend fun deleteTeacher(teacher: Teacher)
    suspend fun deleteCourse(course: Course)

    suspend fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState>
    suspend fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState>
}