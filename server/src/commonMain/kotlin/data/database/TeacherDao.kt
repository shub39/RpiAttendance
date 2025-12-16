package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {
    @Upsert
    suspend fun upsert(teacherEntity: TeacherEntity)

    @Delete
    suspend fun delete(teacherEntity: TeacherEntity)

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: Long): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE biometricId = :biometricId")
    suspend fun getTeacherByBiometricId(biometricId: String): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE firstName LIKE '%' || :name || '%'")
    suspend fun getTeacherByName(name: String): List<TeacherEntity>

    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>
}