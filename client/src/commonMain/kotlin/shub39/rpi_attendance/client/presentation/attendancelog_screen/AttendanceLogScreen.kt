package shub39.rpi_attendance.client.presentation.attendancelog_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@Composable
fun AttendanceLogScreen(
    modifier: Modifier = Modifier,
    state: AttendanceLogState,
    onAction: (AttendanceLogAction) -> Unit
) {

}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AttendanceLogScreen(
            state = AttendanceLogState(),
            onAction = {}
        )
    }
}