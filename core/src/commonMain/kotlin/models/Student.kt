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
 * Represents a student entity in the system. This data class holds all the essential information
 * about a student. It is serializable, allowing it to be easily converted to/from formats like
 * JSON.
 *
 * @property id The unique identifier for the student. Defaults to 0 for new students.
 * @property biometricId The unique biometric identifier for the student, if available. Can be null.
 * @property firstName The student's first name.
 * @property lastName The student's last name.
 * @property rollNo The student's unique roll number.
 * @property contactEmail The student's primary email address for communication.
 * @property contactPhone The student's primary phone number for communication.
 */
@Serializable
data class Student(
    val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val rollNo: Int,
    val contactEmail: String,
    val contactPhone: String,
)
