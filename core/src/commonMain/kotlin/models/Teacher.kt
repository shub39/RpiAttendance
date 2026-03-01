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
 * Represents a teacher in the school system.
 *
 * This data class holds the essential information about a teacher, including their unique
 * identifiers and personal details.
 *
 * @property id The unique database identifier for the teacher. Defaults to 0 for new entries.
 * @property biometricId The optional unique identifier from a biometric system (e.g., fingerprint
 *   or facial scan). Can be null.
 * @property firstName The teacher's first name.
 * @property lastName The teacher's last name.
 * @property subjectTaught The primary subject the teacher is responsible for teaching.
 */
@Serializable
data class Teacher(
    val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val subjectTaught: String,
)
