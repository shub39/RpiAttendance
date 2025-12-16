package data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import domain.AttendanceStatus
import domain.EntityType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Entity(
    tableName = "attendance_log",
    indices = [
        Index(value = ["biometricId"]),
        Index(value = ["entityType"]),
        Index(value = ["entityId"]),
        Index(value = ["timeStamp"]),
        Index(value = ["attendanceStatus"])
    ]
)
data class AttendanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val biometricId: String,
    val entityType: EntityType,
    val entityId: Long,
    val timeStamp: Instant,
    val attendanceStatus: AttendanceStatus
)