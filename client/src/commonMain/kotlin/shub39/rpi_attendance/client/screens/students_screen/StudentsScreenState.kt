package shub39.rpi_attendance.client.screens.students_screen

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import shub39.rpi_attendance.client.StudentsByCourse

@Stable
@Immutable
data class StudentsScreenState(
    val studentsByCourses: List<StudentsByCourse> = emptyList(),
    val selectedStudentsByCourse: StudentsByCourse? = null,
)
