package shub39.rpi_attendance.client.presentation.students_screen

import models.Student

sealed interface StudentsScreenAction {
    data class DeleteStudent(val student: Student): StudentsScreenAction
    data class UpsertStudent(val student: Student): StudentsScreenAction
    data class EnrollStudent(val student: Student): StudentsScreenAction
    data object ResetEnrollState: StudentsScreenAction
}