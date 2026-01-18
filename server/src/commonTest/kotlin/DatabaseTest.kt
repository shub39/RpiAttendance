import data.database.AttendanceLogEntity
import data.database.StudentEntity
import data.database.TeacherEntity
import data.database.getRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import models.AttendanceStatus
import models.EntityType
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

@OptIn(ExperimentalNativeApi::class)
class DatabaseTest {
    val database = getRoomDatabase()

    val teacherDao = database.teacherDao()
    val studentDao = database.studentDao()
    val attendanceLogDao = database.attendanceLogDao()

    val teachers = (0..3).map {
        TeacherEntity(
            biometricId = "teacher_$it",
            firstName = "Teacher $it",
            lastName = "Teacher $it",
            subjectTaught = "Subject $it",
        )
    }

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun teachersTest() = testIn("Teachers Test") {
        teachers.forEach { teacherDao.upsert(it) }
        val teachersFromDb = teacherDao.getAllTeachers().first()
        println(teachersFromDb)

        assertEquals(teachersFromDb.isNotEmpty(), true)
        assertEquals(teachersFromDb.size, teachers.size)
    }

    @Test
    fun studentsTest() = testIn("Students Test") {
        val students = (0..30).map { no ->
                StudentEntity(
                    biometricId = "student_$no",
                    firstName = "Student $no",
                    lastName = "Student $no",
                    contactEmail = "6789@$no",
                    contactPhone = "76125t76$no",
                    rollNo = no
                )
            }

        students.forEach { studentDao.upsert(it) }
        val studentsFromDb = studentDao.getAllStudents().first()
        println(studentsFromDb)
        assertEquals(studentsFromDb.isNotEmpty(), true)
        assertEquals(studentsFromDb.size, students.size)
    }

    @Test
    fun attendanceLogsTest() = testIn("Attendance Logs Test") {
        val studentsFromDb = studentDao.getAllStudents().first()
        val attendanceLogs = studentsFromDb.map { students ->
            AttendanceLogEntity(
                biometricId = students.biometricId ?: "student_${students.id}",
                entityType = EntityType.STUDENT,
                entityId = students.id,
                timeStamp = Clock.System.now(),
                attendanceStatus = AttendanceStatus.IN
            )
        }

        attendanceLogs.forEach { attendanceLogDao.upsert(it) }
        val attendanceLogsFromDb = attendanceLogDao.getAttendanceLogs().first()
        println(attendanceLogsFromDb)
        assertEquals(attendanceLogsFromDb.isNotEmpty(), true)
        assertEquals(attendanceLogsFromDb.size, attendanceLogs.size)

        teacherDao.getAllTeachers().first().forEach { teacherDao.delete(it) }
        studentDao.getAllStudents().first().forEach { studentDao.delete(it) }
        attendanceLogDao.getAttendanceLogs().first().forEach { attendanceLogDao.delete(it) }
    }
}