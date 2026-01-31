package models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Represents a single attendance record captured by a biometric device.
 *
 * This data class stores all the relevant information for an attendance event,
 * linking a biometric identifier to a specific entity (like an employee or student)
 * at a precise moment in time, along with their attendance status (e.g., check-in, check-out).
 *
 * @property id The unique identifier for the attendance log entry. Defaults to 0 for new entries.
 * @property biometricId The unique identifier from the biometric device (e.g., fingerprint ID, card ID).
 * @property entityType The type of entity this log belongs to (e.g., EMPLOYEE, STUDENT).
 * @property entityId The specific ID of the entity (e.g., the employee's or student's primary key).
 * @property timeStamp The exact date and time the attendance was recorded, as an [Instant].
 * @property attendanceStatus The status of the attendance log (e.g., CHECK_IN, CHECK_OUT).
 */
@Serializable
data class AttendanceLog(
    val id: Long = 0,
    val biometricId: String,
    val entityType: EntityType,
    val entityId: Long,
    val timeStamp: Instant,
    val attendanceStatus: AttendanceStatus
)
