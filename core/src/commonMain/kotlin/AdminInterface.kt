import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.rpc.annotations.Rpc
import models.Session
import models.Student
import models.Teacher

// what the admin can do
@Rpc
interface AdminInterface {
    suspend fun getStatus(): Boolean

    fun getStudents(): Flow<List<Student>>
    fun getTeachers(): Flow<List<Teacher>>

    suspend fun getSessionsForDate(date: LocalDate): List<Session>

    suspend fun upsertStudent(student: Student)
    suspend fun upsertTeacher(teacher: Teacher)

    suspend fun deleteStudent(student: Student)
    suspend fun deleteTeacher(teacher: Teacher)

    fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState>
    fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState>
}