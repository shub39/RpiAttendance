package shub39.rpi_attendance.client.domain

import kotlinx.coroutines.flow.Flow

interface AppDatastore {
    fun getServerUrl(): Flow<String>
    suspend fun setServerUrl(url: String)
}