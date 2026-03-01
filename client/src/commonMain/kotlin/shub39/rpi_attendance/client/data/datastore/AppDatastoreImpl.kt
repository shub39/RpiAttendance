/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.rpi_attendance.client.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shub39.rpi_attendance.client.domain.AppDatastore

class AppDatastoreImpl(private val datastore: DataStore<Preferences>) : AppDatastore {
    override fun getServerUrl(): Flow<String> =
        datastore.data.map { prefs -> prefs[serverUrlKey] ?: "" }

    override suspend fun setServerUrl(url: String) {
        datastore.edit { it[serverUrlKey] = url }
    }

    companion object {
        private val serverUrlKey = stringPreferencesKey("server_url")
    }
}
