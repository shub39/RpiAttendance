package data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun getDatabaseBuilder(): RoomDatabase.Builder<ServerDatabase> {
    return Room.databaseBuilder<ServerDatabase>(
        name = "server_database.db"
    )
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<ServerDatabase>
): ServerDatabase {
    return builder
        .setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}