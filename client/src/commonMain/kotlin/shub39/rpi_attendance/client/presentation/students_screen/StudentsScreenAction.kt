package shub39.rpi_attendance.client.presentation.students_screen

import io.github.vinceglb.filekit.PlatformFile
import models.Student

sealed interface StudentsScreenAction {
    data class DeleteStudent(val student: Student): StudentsScreenAction
    data class UpsertStudent(val student: Student): StudentsScreenAction
    data class EnrollStudent(val student: Student): StudentsScreenAction
    data class OnChangeSearchQuery(val query: String): StudentsScreenAction
    data object ResetEnrollState: StudentsScreenAction

    data class ImportList(val file: PlatformFile): StudentsScreenAction
}