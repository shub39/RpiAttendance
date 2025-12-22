package shub39.rpi_attendance.client

import androidx.compose.ui.window.singleWindowApplication
import shub39.rpi_attendance.client.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        title = "Rpi Attendance"
    ) {
        App()
    }
}