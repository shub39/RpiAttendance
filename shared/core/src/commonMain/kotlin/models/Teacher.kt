package models

import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val subjectTaught: String,
)
