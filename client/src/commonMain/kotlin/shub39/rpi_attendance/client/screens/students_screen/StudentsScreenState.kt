package shub39.rpi_attendance.client.screens.students_screen

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import models.Student

@Stable
@Immutable
data class StudentsScreenState(
    val students: List<Student> = emptyList()
)
