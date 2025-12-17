import data.database.*
import domain.AttendanceStatus
import domain.EntityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.Clock

@OptIn(ExperimentalNativeApi::class)
class DatabaseTest {
    val database = getRoomDatabase(getDatabaseBuilder())

    val teacherDao = database.teacherDao()
    val studentDao = database.studentDao()
    val attendanceLogDao = database.attendanceLogDao()
    val courseDao = database.courseDao()

    val courses = (0..3).map {
        CourseEntity(
            id = it.toLong(),
            name = "Course $it",
            description = "Test Course",
            code = "123$it"
        )
    }
    val teachers = (0..3).map {
        TeacherEntity(
            biometricId = "teacher_$it",
            firstName = "Teacher $it",
            lastName = "Teacher $it",
            subjectTaught = "Subject $it",
        )
    }
    val students = courses.flatMap { course ->
        (0..30).map { no ->
            StudentEntity(
                courseId = course.id,
                biometricId = "student_$no",
                firstName = "Student $no",
                lastName = "Student $no",
                contactEmail = "6789@$no",
                contactPhone = "76125t76$no"
            )
        }
    }
    val attendanceLogs = students.map { students ->
        AttendanceLogEntity(
            biometricId = students.biometricId ?: "student_${students.id}",
            entityType = EntityType.STUDENT,
            entityId = students.id,
            timeStamp = Clock.System.now(),
            attendanceStatus = AttendanceStatus.IN
        )
    }

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @AfterTest
    fun tearDown() = testIn("Clear DB") {
        courseDao.getAllCourses().first().forEach { courseDao.delete(it) }
    }

    @Test
    fun testInsertion() = testIn("Courses Test") {
        courses.forEach { courseDao.upsert(it) }
        val coursesFromDb = courseDao.getAllCourses().first()
        println(coursesFromDb)

        assert(coursesFromDb.isNotEmpty())
        assert(coursesFromDb.size == courses.size)
    }

}

