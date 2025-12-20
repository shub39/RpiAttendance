package models

import kotlinx.serialization.Serializable

@Serializable
enum class EntityType {
    STUDENT, TEACHER
}