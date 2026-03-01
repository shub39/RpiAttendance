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

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * Represents a single teaching session.
 *
 * This data class holds all the relevant information for a session, including the teacher
 * conducting it, the start and end times, and details about the students attending. It is marked as
 * serializable for data persistence or transfer.
 *
 * @property teacher The [Teacher] responsible for the session.
 * @property startTime The scheduled start time of the session.
 * @property endTime The scheduled end time of the session.
 * @property totalStudents The total number of students enrolled in the session.
 * @property students A list of [Student] objects representing the students attending the session.
 */
@Serializable
data class Session(
    val teacher: Teacher,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val totalStudents: Int,
    val students: List<Student>,
)
