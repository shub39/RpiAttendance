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
package shub39.rpi_attendance.client.presentation.students_screen

import EnrollState
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import models.Student

@Stable
@Immutable
data class StudentsScreenState(
    val areSensorsBusy: Boolean = false,
    val students: List<Student> = emptyList(),
    val enrollState: EnrollState = EnrollState.Idle,
    val searchResults: List<Student> = emptyList(),
    val searchQuery: String = "",
)
