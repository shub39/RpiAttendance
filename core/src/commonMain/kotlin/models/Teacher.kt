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
 * Represents a faculty member in the attendance system.
 *
 * This data class holds the essential information about a faculty member.
 *
 * @property entityId The unique database identifier. Defaults to 0 for new entries.
 * @property id The faculty identifier used for face enrollment and attendance logs.
 * @property name The faculty member's name.
 * @property dept The faculty member's department.
 * @property designation The faculty member's designation.
 */
@Serializable
data class Teacher(
    val entityId: Long = 0,
    val id: String,
    val name: String,
    val dept: String,
    val designation: String,
)
