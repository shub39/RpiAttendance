package shub39.rpi_attendance.client.presentation.attendancelog_screen

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.DetailedAttendanceLog
import kotlin.time.Clock

data class AttendanceLogState(
    val selectedDate: LocalDate =  Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val filteredLogs: List<DetailedAttendanceLog> = emptyList(),
    val allLogs: List<DetailedAttendanceLog> = emptyList()
)

