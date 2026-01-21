package shub39.rpi_attendance.client.presentation.attendancelog_screen

import models.Session

data class AttendanceLogState(
    val sessions: List<Session> = emptyList()
)