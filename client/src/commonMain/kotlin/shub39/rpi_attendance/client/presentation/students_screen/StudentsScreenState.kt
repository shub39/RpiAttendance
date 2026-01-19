package shub39.rpi_attendance.client.presentation.students_screen

import EnrollState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import models.Student

@Stable
@Immutable
data class StudentsScreenState(
    val students: List<Student> = emptyList(),
    val enrollState: EnrollState = EnrollState.Idle
)
