package data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface AttendanceLogDao {
    @Upsert
    suspend fun upsert(attendanceLog: AttendanceLogEntity)

    @Delete
    suspend fun delete(attendanceLog: AttendanceLogEntity)

    @Query("SELECT * FROM attendance_log WHERE biometricId = :biometricId")
    suspend fun getAttendanceLogByBiometricId(biometricId: String): List<AttendanceLogEntity>

    @Query("SELECT * FROM attendance_log WHERE timeStamp >= :startTime AND timeStamp <= :endTime")
    suspend fun getLogsBetween(startTime: Instant, endTime: Instant): List<AttendanceLogEntity>

    @Query("SELECT * FROM attendance_log")
    fun getAttendanceLogs(): Flow<List<AttendanceLogEntity>>
}