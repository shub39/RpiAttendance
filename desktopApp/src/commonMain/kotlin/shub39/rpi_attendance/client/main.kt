package shub39.rpi_attendance.client

import androidx.compose.ui.window.singleWindowApplication
import shub39.rpi_attendance.ui.App
import shub39.rpi_attendance.ui.di.initKoin

fun main() {
    initKoin()

    singleWindowApplication(
        title = "Rpi Attendance"
    ) {
        App()
    }
}