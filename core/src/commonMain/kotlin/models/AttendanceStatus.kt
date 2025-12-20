package models

import kotlinx.serialization.Serializable

@Serializable
enum class AttendanceStatus {
    IN, OUT
}