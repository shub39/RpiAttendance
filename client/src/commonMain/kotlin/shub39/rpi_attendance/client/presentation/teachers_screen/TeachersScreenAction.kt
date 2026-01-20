package shub39.rpi_attendance.client.presentation.teachers_screen

import models.Teacher

sealed interface TeachersScreenAction {
    data class DeleteTeacher(val teacher: Teacher): TeachersScreenAction
    data class UpsertTeacher(val teacher: Teacher): TeachersScreenAction
    data class EnrollTeacher(val teacher: Teacher): TeachersScreenAction
    data class OnChangeSearchQuery(val query: String): TeachersScreenAction
    data object ResetEnrollState: TeachersScreenAction
}