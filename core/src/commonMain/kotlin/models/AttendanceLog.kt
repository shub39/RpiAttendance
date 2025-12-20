package models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class AttendanceLog(
    val id: Long = 0,
    val biometricId: String,
    val entityType: EntityType,
    val entityId: Long,
    val timeStamp: Instant,
    val attendanceStatus: AttendanceStatus
)
