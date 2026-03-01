/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.rpi_attendance.client.presentation.attendancelog_screen

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.DetailedAttendanceLog

data class AttendanceLogState(
    val selectedDate: LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val filteredDetailedLogs: List<DetailedAttendanceLog> = emptyList(),
    val allDetailedLogs: List<DetailedAttendanceLog> = emptyList(),
)
