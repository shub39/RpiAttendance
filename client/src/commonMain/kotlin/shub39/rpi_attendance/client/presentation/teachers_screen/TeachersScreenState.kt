package shub39.rpi_attendance.client.presentation.teachers_screen

import EnrollState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import models.Teacher

@Stable
@Immutable
data class TeachersScreenState(
    val teachers: List<Teacher> = emptyList(),
    val enrollState: EnrollState = EnrollState.Idle,
    val searchResults: List<Teacher> = emptyList(),
    val searchQuery: String = ""
)