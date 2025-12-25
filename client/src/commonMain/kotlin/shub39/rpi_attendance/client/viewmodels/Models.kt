package shub39.rpi_attendance.client.viewmodels

import models.Course
import models.Student
import models.Teacher

typealias StudentsByCourse = Pair<Course, List<Student>>

data class Models(
    val students: List<Student> = emptyList(),
    val teachers: List<Teacher> = emptyList(),
    val courses: List<Course> = emptyList(),
    val studentsByCourses: List<StudentsByCourse> = emptyList()
)