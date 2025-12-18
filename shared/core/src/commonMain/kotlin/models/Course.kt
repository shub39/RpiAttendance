package models

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: Long = 0,
    val name: String,
    val description: String,
    val code: String,
)
