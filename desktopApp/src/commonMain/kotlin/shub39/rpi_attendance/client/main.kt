package shub39.rpi_attendance.client

import androidx.compose.ui.window.singleWindowApplication
import io.github.vinceglb.filekit.FileKit
import shub39.rpi_attendance.client.di.initKoin

fun main() {
    initKoin()
    FileKit.init("rpi_attendance")

    singleWindowApplication(
        title = "Rpi Attendance"
    ) {
        App()
    }
}