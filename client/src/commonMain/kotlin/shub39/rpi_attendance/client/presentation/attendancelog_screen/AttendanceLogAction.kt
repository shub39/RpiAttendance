package shub39.rpi_attendance.client.presentation.attendancelog_screen

sealed interface AttendanceLogAction {
    data object OnGetSessions: AttendanceLogAction
}