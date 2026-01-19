package models

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val rollNo: Int,
    val contactEmail: String,
    val contactPhone: String
)
