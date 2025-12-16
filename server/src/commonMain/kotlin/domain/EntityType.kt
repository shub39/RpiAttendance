package domain

import kotlinx.serialization.Serializable

@Serializable
enum class EntityType {
    STUDENT, TEACHER
}