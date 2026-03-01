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

import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * Represents a single attendance record captured by a biometric device.
 *
 * This data class stores all the relevant information for an attendance event, linking a biometric
 * identifier to a specific entity (like an employee or student) at a precise moment in time, along
 * with their attendance status (e.g., check-in, check-out).
 *
 * @property id The unique identifier for the attendance log entry. Defaults to 0 for new entries.
 * @property biometricId The unique identifier from the biometric device (e.g., fingerprint ID, card
 *   ID).
 * @property entityType The type of entity this log belongs to (e.g., EMPLOYEE, STUDENT).
 * @property entityId The specific ID of the entity (e.g., the employee's or student's primary key).
 * @property timeStamp The exact date and time the attendance was recorded, as an [Instant].
 * @property attendanceStatus The status of the attendance log (e.g., CHECK_IN, CHECK_OUT).
 */
@Serializable
data class AttendanceLog(
    val id: Long = 0,
    val biometricId: String,
    val entityType: EntityType,
    val entityId: Long,
    val timeStamp: Instant,
    val attendanceStatus: AttendanceStatus,
)
