package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Upsert
    suspend fun upsert(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

//    @Query("SELECT * FROM students WHERE id = :id")
//    suspend fun getStudentById(id: Int): StudentEntity?
//
//    @Query("SELECT * FROM students WHERE biometricId = :biometricId")
//    suspend fun getStudentByBiometricId(biometricId: String): StudentEntity?
//
//    @Query("SELECT * FROM students WHERE firstName LIKE '%' || :name || '%'")
//    suspend fun getStudentsByName(name: String): List<StudentEntity>
//
//    @Query("SELECT * FROM students WHERE courseId = :courseId")
//    suspend fun getStudentsByCourseId(courseId: Long): List<StudentEntity>

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>
}