package models

import kotlinx.serialization.Serializable

/**
 * Represents the attendance status of a user.
 * This is used to track whether a user is currently checked in or out.
 */
@Serializable
enum class AttendanceStatus {
    IN, OUT
}