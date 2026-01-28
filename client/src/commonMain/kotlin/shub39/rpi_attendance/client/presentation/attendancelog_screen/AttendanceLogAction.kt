package shub39.rpi_attendance.client.presentation.attendancelog_screen

import kotlinx.datetime.LocalDate

sealed interface AttendanceLogAction {
    data class OnGetSessions(val date: LocalDate): AttendanceLogAction
}