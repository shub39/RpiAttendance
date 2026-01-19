package shub39.rpi_attendance.client.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import shub39.rpi_attendance.client.domain.Route
import shub39.rpi_attendance.client.presentation.students_screen.StudentsScreen
import shub39.rpi_attendance.client.viewmodels.StudentsScreenViewModel

@Composable
fun MainScreens(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                onNavigate = {
                    navController.navigate(it) {
                        launchSingleTop = true
                        popUpTo(Route.StudentsScreen)
                    }
                },
                navController = navController
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.StudentsScreen
        ) {
            composable<Route.StudentsScreen> {
                val viewModel = koinInject<StudentsScreenViewModel>()
                val state by viewModel.state.collectAsState()

                StudentsScreen(
                    contentPadding = padding,
                    state = state,
                    onAction = viewModel::onAction
                )
            }
            composable<Route.TeachersScreen> {

            }
            composable<Route.AttendanceLogScreen> {

            }
        }
    }
}

@Composable
private fun NavigationBar(
    modifier: Modifier = Modifier,
    onNavigate: (Route) -> Unit,
    navController: NavController
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = navBackStackEntry?.destination

        Route.screens.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(item.route::class)
                } == true,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = stringResource(item.title)
                    )
                },
                label = {
                    Text(stringResource(item.title))
                },
                alwaysShowLabel = false
            )
        }
    }
}