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
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.rpc.annotations.Rpc
import models.AttendanceLog
import models.DetailedAttendanceLog
import models.Session
import models.Student
import models.Teacher

/**
 * Defines the remote procedure call (RPC) interface for administrative actions. This interface
 * provides methods for managing students, teachers, attendance logs, and biometric data within the
 * system. It is intended to be used by the admin client to interact with the server.
 */
@Rpc
interface AdminInterface {
    suspend fun getStatus(): Boolean

    fun getAreSensorsBusy(): Flow<Boolean>

    fun getStudents(): Flow<List<Student>>

    fun getTeachers(): Flow<List<Teacher>>

    fun getDetailedAttendanceLogs(): Flow<List<DetailedAttendanceLog>>

    suspend fun getSessionsForDate(date: LocalDate): List<Session>

    suspend fun upsertStudent(student: Student)

    suspend fun upsertTeacher(teacher: Teacher)

    suspend fun deleteStudent(student: Student)

    suspend fun deleteTeacher(teacher: Teacher)

    suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog)

    fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState>

    fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState>
}
