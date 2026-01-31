package shub39.rpi_attendance.client.domain

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.attendance_log
import rpiattendance.client.generated.resources.folder_eye
import rpiattendance.client.generated.resources.list_alt_check
import rpiattendance.client.generated.resources.person_book
import rpiattendance.client.generated.resources.school
import rpiattendance.client.generated.resources.sessions
import rpiattendance.client.generated.resources.students
import rpiattendance.client.generated.resources.teachers

/**
 * Represents an item in the navigation UI, such as a navigation drawer or bottom bar.
 * It encapsulates all the necessary information to display a navigable screen link.
 *
 * @property route The destination [Route] to navigate to when this item is selected.
 * @property icon The [DrawableResource] to be displayed as the icon for this navigation item.
 * @property title The [StringResource] for the text label of this navigation item.
 */
data class NavigationItem(
    val route: Route,
    val icon: DrawableResource,
    val title: StringResource
)

/**
 * Represents the different screens/destinations in the application's navigation graph.
 * This sealed interface is used by the navigation component to identify which screen to display.
 */
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
                icon = Res.drawable.folder_eye,
                title = Res.string.sessions
            )
        )
    }
}