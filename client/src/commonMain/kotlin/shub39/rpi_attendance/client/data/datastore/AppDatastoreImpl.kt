package shub39.rpi_attendance.client.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.rpi_attendance.client.domain.AppDatastore

class AppDatastoreImpl(
    private val datastore: DataStore<Preferences>
): AppDatastore {
    override fun getServerUrl(): Flow<String> = datastore.data
        .map { prefs ->
            prefs[serverUrlKey] ?: ""
        }


    override suspend fun setServerUrl(url: String) {
        datastore.edit {
            it[serverUrlKey] = url
        }
    }

    companion object {
        private val serverUrlKey = stringPreferencesKey("server_url")
    }
}