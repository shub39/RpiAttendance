package shub39.rpi_attendance.client.presentation.attendancelog_screen

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Session
import kotlin.time.Clock

@Stable
@Immutable
data class AttendanceLogState(
    val sessions: List<Session> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
)