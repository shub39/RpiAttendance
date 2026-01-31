package models

import kotlinx.serialization.Serializable

/**
 * Represents the type of an entity within the system.
 * This is used to differentiate between different roles, such as students and teachers.
 */
@Serializable
enum class EntityType {
    STUDENT, TEACHER
}