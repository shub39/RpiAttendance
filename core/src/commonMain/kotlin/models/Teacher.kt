package models

import kotlinx.serialization.Serializable

/**
 * Represents a teacher in the school system.
 *
 * This data class holds the essential information about a teacher,
 * including their unique identifiers and personal details.
 *
 * @property id The unique database identifier for the teacher. Defaults to 0 for new entries.
 * @property biometricId The optional unique identifier from a biometric system (e.g., fingerprint or facial scan). Can be null.
 * @property firstName The teacher's first name.
 * @property lastName The teacher's last name.
 * @property subjectTaught The primary subject the teacher is responsible for teaching.
 */
@Serializable
data class Teacher(
    val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val subjectTaught: String,
)