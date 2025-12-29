package shub39.rpi_attendance.client.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object StudentsScreen: Route

    @Serializable
    data object TeachersScreen: Route

    @Serializable
    data object AttendanceLogScreen: Route

    companion object {
        val screens = listOf<Route>(
            AttendanceLogScreen,
            TeachersScreen,
            StudentsScreen
        )
    }
}