package models

import kotlinx.serialization.Serializable

/**
 * Represents a student entity in the system.
 * This data class holds all the essential information about a student.
 * It is serializable, allowing it to be easily converted to/from formats like JSON.
 *
 * @property id The unique identifier for the student. Defaults to 0 for new students.
 * @property biometricId The unique biometric identifier for the student, if available. Can be null.
 * @property firstName The student's first name.
 * @property lastName The student's last name.
 * @property rollNo The student's unique roll number.
 * @property contactEmail The student's primary email address for communication.
 * @property contactPhone The student's primary phone number for communication.
 */
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
