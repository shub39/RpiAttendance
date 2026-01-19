package shub39.rpi_attendance.client.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

actual class DataStoreFactory {
    actual fun getPreferencesDataStore(): DataStore<Preferences> {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "RpiAttendance")
            os.contains("mac") -> File(userHome, "Library/Application Support/RpiAttendance")
            else -> File(userHome, ".local/share/RpiAttendance")
        }

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, preferencesFileName)
        return createDataStore(
            producePath = { dbFile.absolutePath }
        )
    }
}