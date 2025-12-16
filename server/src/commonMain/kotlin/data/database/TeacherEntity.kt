package data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val biometricId: String?,
    val firstName: String,
    val lastName: String,
    val subjectTaught: String,
)