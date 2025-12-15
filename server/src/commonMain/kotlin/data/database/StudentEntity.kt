package data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "students"
)
data class StudentEntity(
    @PrimaryKey val id: Int,
)
