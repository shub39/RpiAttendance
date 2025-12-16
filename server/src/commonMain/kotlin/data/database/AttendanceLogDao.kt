package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceLogDao {
    @Upsert
    suspend fun upsert(attendanceLog: AttendanceLogEntity)

    @Delete
    suspend fun delete(attendanceLog: AttendanceLogEntity)

    @Query("SELECT * FROM attendance_log WHERE id = :id")
    suspend fun getAttendanceLogById(id: Long): AttendanceLogEntity?

    @Query("SELECT * FROM attendance_log")
    fun getAttendanceLogs(): Flow<List<AttendanceLogEntity>>
}