package shub39.rpi_attendance.client.screens.students_screen

import models.Student

sealed interface StudentsScreenAction {
    data class DeleteStudent(val student: Student): StudentsScreenAction
    data class EnrollStudent(val student: Student): StudentsScreenAction
}