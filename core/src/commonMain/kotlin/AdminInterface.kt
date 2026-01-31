import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.rpc.annotations.Rpc
import models.AttendanceLog
import models.DetailedAttendanceLog
import models.Session
import models.Student
import models.Teacher

/**
 * Defines the remote procedure call (RPC) interface for administrative actions.
 * This interface provides methods for managing students, teachers, attendance logs,
 * and biometric data within the system. It is intended to be used by the admin client
 * to interact with the server.
 */
@Rpc
interface AdminInterface {
    suspend fun getStatus(): Boolean

    fun getStudents(): Flow<List<Student>>
    fun getTeachers(): Flow<List<Teacher>>
    fun getDetailedAttendanceLogs(): Flow<List<DetailedAttendanceLog>>

    suspend fun getSessionsForDate(date: LocalDate): List<Session>

    suspend fun upsertStudent(student: Student)
    suspend fun upsertTeacher(teacher: Teacher)

    suspend fun deleteStudent(student: Student)
    suspend fun deleteTeacher(teacher: Teacher)
    suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog)

    fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState>
    fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState>
}