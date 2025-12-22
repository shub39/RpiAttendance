package shub39.rpi_attendance.client.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

data class Database(
    val students: List<Student> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val courses: List<Course> = emptyList(),
    val attendanceLogs: List<AttendanceLog> = emptyList()
)

class StateLayer {
    val database = MutableStateFlow(Database())
}