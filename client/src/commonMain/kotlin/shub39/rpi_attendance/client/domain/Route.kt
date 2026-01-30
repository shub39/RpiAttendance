package shub39.rpi_attendance.client.domain

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.attendance_log
import rpiattendance.client.generated.resources.csv
import rpiattendance.client.generated.resources.list_alt_check
import rpiattendance.client.generated.resources.person_book
import rpiattendance.client.generated.resources.school
import rpiattendance.client.generated.resources.sessions
import rpiattendance.client.generated.resources.students
import rpiattendance.client.generated.resources.teachers

data class NavigationItem(
    val route: Route,
    val icon: DrawableResource,
    val title: StringResource
)

@Stable
@Serializable
sealed interface Route {
    @Serializable
    data object StudentsScreen: Route

    @Serializable
    data object TeachersScreen: Route

    @Serializable
    data object AttendanceLogScreen: Route

    @Serializable
    data object SessionsScreen: Route

    companion object {
        val screens = listOf(
            NavigationItem(
                route = StudentsScreen,
                icon = Res.drawable.person_book,
                title = Res.string.students
            ),
            NavigationItem(
                route = TeachersScreen,
                icon = Res.drawable.school,
                title = Res.string.teachers
            ),
            NavigationItem(
                route = AttendanceLogScreen,
                icon = Res.drawable.list_alt_check,
                title = Res.string.attendance_log
            ),
            NavigationItem(
                route = SessionsScreen,
                icon = Res.drawable.csv,
                title = Res.string.sessions
            )
        )
    }
}