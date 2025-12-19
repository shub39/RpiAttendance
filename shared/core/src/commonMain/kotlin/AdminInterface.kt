import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

// what the admin can do
@Rpc
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

    fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState>
    fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState>
}