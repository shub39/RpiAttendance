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
package data

import AdminInterface
import EnrollState
import data.database.AttendanceLogDao
import data.database.TeacherDao
import domain.SensorServer
import errors.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import logInfo
import models.AttendanceLog
import models.DetailedAttendanceLog
import models.EntityType
import models.Session
import models.Student
import models.Teacher

class AdminInterfaceImpl(
    private val teacherDao: TeacherDao,
    private val attendanceLogDao: AttendanceLogDao,
    private val sensorServer: SensorServer,
) : AdminInterface {
    override suspend fun getStatus(): Boolean = true

    override fun getAreSensorsBusy(): Flow<Boolean> = sensorServer.areSensorsBusy

    override fun getStudents(): Flow<List<Student>> = flowOf(emptyList())

    override fun getTeachers(): Flow<List<Teacher>> =
        teacherDao
            .getAllTeachers()
            .map { flow -> flow.map { teachers -> teachers.toTeacher() } }
            .flowOn(Dispatchers.IO)

    override fun getDetailedAttendanceLogs(): Flow<List<DetailedAttendanceLog>> =
        attendanceLogDao
            .getAttendanceLogs()
            .map { flow ->
                flow.mapNotNull { log ->
                    when (log.entityType) {
                        EntityType.TEACHER -> {
                            teacherDao.getTeacherByEntityId(log.entityId)?.let { teacherEntity ->
                                DetailedAttendanceLog(
                                    teacher = teacherEntity.toTeacher(),
                                    log = log.toAttendanceLog(),
                                )
                            }
                        }

                        else -> null
                    }
                }
            }
            .flowOn(Dispatchers.IO)

    override suspend fun getSessionsForDate(date: LocalDate): List<Session> = emptyList()

    override suspend fun upsertStudent(student: Student) = Unit

    override suspend fun upsertTeacher(teacher: Teacher) {
        logInfo("upserting faculty $teacher")
        val presentTeacher = teacherDao.getTeacherById(teacher.id)
        if (presentTeacher != null && presentTeacher.entityId != teacher.entityId) {
            teacherDao.delete(presentTeacher)
        }
        teacherDao.upsert(teacher.toTeacherEntity())
    }

    override suspend fun deleteStudent(student: Student) = Unit

    override suspend fun deleteTeacher(teacher: Teacher) {
        logInfo("deleting faculty $teacher")
        sensorServer.deleteFace(teacher.id)
        teacherDao.delete(teacher.toTeacherEntity())
    }

    override suspend fun deleteAttendanceLog(attendanceLog: AttendanceLog) {
        logInfo("deleting attendance log $attendanceLog")
        attendanceLogDao.delete(attendanceLog.toAttendanceLogEntity())
    }

    override fun addBiometricDetailsForStudent(student: Student): Flow<EnrollState> =
        flowOf(EnrollState.EnrollFailed("Student enrollment is disabled"))

    override fun addBiometricDetailsForTeacher(teacher: Teacher): Flow<EnrollState> =
        addBiometricDetails(
            faceId = teacher.id,
            name = teacher.name,
            dept = teacher.dept,
            designation = teacher.designation,
        )

    private fun addBiometricDetails(
        faceId: String,
        name: String,
        dept: String,
        designation: String,
    ): Flow<EnrollState> = flow {
        sensorServer.updateAdminOperationStatus(true)
        emit(EnrollState.Enrolling)

        sensorServer.displayText(listOf("Enrolling", "Face"))

        when (
            val faceResult =
                sensorServer.enrollFace(
                    id = faceId,
                    name = name,
                    dept = dept,
                    designation = designation,
                )
        ) {
            is Result.Error -> {
                emit(EnrollState.EnrollFailed(faceResult.debugMessage))
                sensorServer.displayText(listOf("Enroll", "Failed!"))
            }

            is Result.Success -> {
                emit(EnrollState.EnrollComplete)
                sensorServer.displayText(listOf("Enroll", "Complete!"))
            }
        }

        delay(1000)
        sensorServer.displayText(listOf("Detecting", "Faces"))
        sensorServer.updateAdminOperationStatus(false)
    }
}
