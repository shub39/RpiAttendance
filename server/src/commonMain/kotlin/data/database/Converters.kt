package data.database

import androidx.room.TypeConverter
import kotlin.time.Instant

object Converters {
    @TypeConverter
    fun instantToTimeStamp(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun timeStampToInstant(timeStamp: Long): Instant {
        return Instant.fromEpochMilliseconds(timeStamp)
    }
}