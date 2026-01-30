package shub39.rpi_attendance.client.presentation.sessions_screen

import kotlinx.datetime.LocalDate

sealed interface SessionsAction {
    data class OnGetSessions(val date: LocalDate): SessionsAction
}