package shub39.rpi_attendance.client.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import shub39.rpi_attendance.client.navigation.Route

@Composable
fun MainScreens(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.StudentsScreen,
        modifier = modifier
    ) {
        composable<Route.StudentsScreen> {

        }
        composable<Route.TeachersScreen> {

        }
        composable<Route.AttendanceLogScreen> {

        }
    }
}