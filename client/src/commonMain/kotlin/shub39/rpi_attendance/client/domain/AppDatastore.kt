package shub39.rpi_attendance.client.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for accessing and modifying app-level persisted data.
 * This acts as a contract for data storage, abstracting the underlying
 * implementation (e.g., Preferences DataStore).
 */
interface AppDatastore {
    fun getServerUrl(): Flow<String>
    suspend fun setServerUrl(url: String)
}