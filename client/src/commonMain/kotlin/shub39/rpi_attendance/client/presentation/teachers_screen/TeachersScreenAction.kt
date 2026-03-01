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
package shub39.rpi_attendance.client.presentation.teachers_screen

import io.github.vinceglb.filekit.PlatformFile
import models.Teacher

sealed interface TeachersScreenAction {
    data class DeleteTeacher(val teacher: Teacher) : TeachersScreenAction

    data class UpsertTeacher(val teacher: Teacher) : TeachersScreenAction

    data class EnrollTeacher(val teacher: Teacher) : TeachersScreenAction

    data class OnChangeSearchQuery(val query: String) : TeachersScreenAction

    data object ResetEnrollState : TeachersScreenAction

    data class ImportList(val file: PlatformFile) : TeachersScreenAction
}
