package shub39.rpi_attendance.client.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = Color.Yellow,
        content = content
    )
}