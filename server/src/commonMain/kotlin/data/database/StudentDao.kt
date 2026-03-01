/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Upsert suspend fun upsert(student: StudentEntity)

    @Delete suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE id IN (:ids)")
    suspend fun getStudentsByIds(ids: List<Long>): List<StudentEntity>

    @Query("SELECT * FROM students WHERE biometricId = :biometricId")
    suspend fun getStudentByBiometricId(biometricId: String): StudentEntity?

    @Query("SELECT * FROM students") fun getAllStudents(): Flow<List<StudentEntity>>
}
