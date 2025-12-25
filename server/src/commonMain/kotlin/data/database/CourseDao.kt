package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Upsert
    suspend fun upsert(course: CourseEntity)

    @Delete
    suspend fun delete(course: CourseEntity)

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourse(id: Long): CourseEntity?

    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>
}