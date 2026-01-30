package shub39.rpi_attendance.client.presentation.attendancelog_screen

import kotlinx.datetime.LocalDate
import models.AttendanceLog

sealed interface AttendanceLogAction {
    data class OnLoadDate(val date: LocalDate): AttendanceLogAction
    data class OnDeleteLog(val log: AttendanceLog): AttendanceLogAction
}