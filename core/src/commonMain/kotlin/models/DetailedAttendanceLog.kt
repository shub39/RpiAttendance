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
package models

import kotlinx.serialization.Serializable

/**
 * Represents a detailed attendance log, combining a raw [AttendanceLog] with information about the
 * user (either a [Teacher] or a [Student]) who created it. This sealed interface allows for
 * polymorphic serialization and handling of logs from different user types.
 *
 * @property log The underlying raw attendance log entry.
 */
@Serializable
sealed interface DetailedAttendanceLog {
    val log: AttendanceLog

    @Serializable
    data class TeacherLog(val teacher: Teacher, override val log: AttendanceLog) :
        DetailedAttendanceLog

    @Serializable
    data class StudentLog(val student: Student, override val log: AttendanceLog) :
        DetailedAttendanceLog
}
