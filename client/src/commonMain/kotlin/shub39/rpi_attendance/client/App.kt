package shub39.rpi_attendance.client

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.app_name
import rpiattendance.client.generated.resources.connect
import rpiattendance.client.generated.resources.enter_ip
import shub39.rpi_attendance.client.screens.MainScreens
import shub39.rpi_attendance.client.viewmodels.AppViewModel

@Serializable
sealed interface MainRoutes {
    @Serializable
    data object StudentList: MainRoutes

    @Serializable
    data object TeacherList: MainRoutes

    @Serializable
    data object AttendanceLog: MainRoutes
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    mainViewModel: AppViewModel = koinInject()
) {
    val isInterfaceChecked by mainViewModel.isInterfaceChecked.collectAsState()

    AppContent(
        modifier = modifier,
        isInterfaceChecked = isInterfaceChecked,
        onCheckIp = { mainViewModel.checkUrl(it) },
        onSetUrl = { mainViewModel.setUrl(it) }
    )
}

@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    isInterfaceChecked: Boolean,
    onCheckIp: (String) -> Unit,
    onSetUrl: (String) -> Unit
) {
    var showApp by remember { mutableStateOf(false) }

    AnimatedContent(
        modifier = modifier,
        targetState = showApp
    ) { showAppContent ->
        if (!showAppContent) {
            // prompt to enter Ip
            var ip by remember { mutableStateOf("") }

            Surface {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ip,
                        onValueChange = {
                            ip = it
                            onCheckIp(it)
                        },
                        label = { Text(stringResource(Res.string.enter_ip)) },
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showApp = true
                            onSetUrl(ip)
                        },
                        enabled = isInterfaceChecked
                    ) {
                        Text(text = stringResource(Res.string.connect))
                    }
                }
            }
        } else {
            MainScreens()
        }
    }
}