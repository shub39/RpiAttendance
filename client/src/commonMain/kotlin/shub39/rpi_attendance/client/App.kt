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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.app_name
import rpiattendance.client.generated.resources.connect
import rpiattendance.client.generated.resources.enter_ip
import shub39.rpi_attendance.client.presentation.MainScreens
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import shub39.rpi_attendance.client.viewmodels.AppViewModel

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    val mainViewModel: AppViewModel = koinInject()
    val serverUrl by mainViewModel.serverUrl.collectAsState()
    val isInterfaceChecked by mainViewModel.isInterfaceChecked.collectAsState()

    AppTheme {
        AppContent(
            modifier = modifier,
            isInterfaceChecked = isInterfaceChecked,
            serverUrl = serverUrl,
            onSetUrl = { mainViewModel.updateServerUrl(it) },
            onSaveUrl = { mainViewModel.saveUrl(it) }
        )
    }
}

@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    serverUrl: String,
    isInterfaceChecked: Boolean,
    onSetUrl: (String) -> Unit,
    onSaveUrl: (String) -> Unit
) {
    var showApp by remember { mutableStateOf(false) }

    AnimatedContent(
        modifier = modifier,
        targetState = showApp
    ) { showAppContent ->
        if (!showAppContent) {
            // prompt to enter Ip
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
                        value = serverUrl,
                        onValueChange = { onSetUrl(it) },
                        label = { Text(stringResource(Res.string.enter_ip)) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSaveUrl(serverUrl)
                            showApp = true
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