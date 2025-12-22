package shub39.rpi_attendance.client.viewmodels

import kotlinx.datetime.LocalDate
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

typealias StudentsByCourse = Pair<Course, List<Student>>
typealias AttendanceLogsByDate = Pair<LocalDate, List<AttendanceLog>>

data class Models(
    val students: List<Student> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val courses: List<Course> = emptyList(),
    val studentsByCourses: List<StudentsByCourse> = emptyList(),
    val attendanceLogs: List<AttendanceLog> = emptyList(),
    val attendanceLogsByDates: List<AttendanceLogsByDate> = emptyList()
)