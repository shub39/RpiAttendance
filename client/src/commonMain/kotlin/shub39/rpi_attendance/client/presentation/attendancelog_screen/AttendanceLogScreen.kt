package shub39.rpi_attendance.client.presentation.attendancelog_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.arrow_back
import rpiattendance.client.generated.resources.arrow_forward
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import shub39.rpi_attendance.client.presentation.toFormattedString

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceLogScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    state: AttendanceLogState,
    onAction: (AttendanceLogAction) -> Unit
) {
    Scaffold(
        modifier = modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Attendance Logs")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalIconButton(
                    onClick = {},
                    shape = ButtonGroupDefaults.connectedLeadingButtonShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }

                FilledTonalButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = ButtonGroupDefaults.connectedMiddleButtonPressShape
                ) {
                    Text(
                        text = state.selectedDate.toFormattedString()
                    )
                }

                FilledTonalIconButton(
                    onClick = {},
                    shape = ButtonGroupDefaults.connectedTrailingButtonShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AttendanceLogScreen(
            state = AttendanceLogState(),
            onAction = {},
            padding = PaddingValues()
        )
    }
}