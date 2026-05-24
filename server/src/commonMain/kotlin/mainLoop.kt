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
import data.database.AttendanceLogDao
import data.database.TeacherDao
import data.toAttendanceLogEntity
import domain.FaceEvent
import domain.KeypadResult
import domain.SensorServer
import errors.Result
import errors.onSuccess
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AttendanceLog
import models.AttendanceStatus
import models.EntityType

suspend fun mainLoop(
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    sensorServer: SensorServer,
) {
    sensorServer.displayText(listOf("Rpiattendance", "by shub39"))
    delay(2000)

    var lastFaceEventTimestamp: Double? = null

    while (true) {
        if (sensorServer.isAdminOperationActive.first()) {
            logInfo("Admin Operation in process")
            delay(1000)
            continue
        }

        when (val faceEvents = sensorServer.getFaceEvents(lastFaceEventTimestamp)) {
            is Result.Error -> logError("Face Events", faceEvents.error, faceEvents.debugMessage)
            is Result.Success -> {
                faceEvents.data.events.forEach { event ->
                    lastFaceEventTimestamp = event.timestamp
                    handleFaceEvent(
                        event = event,
                        sensorServer = sensorServer,
                        teacherDao = teacherDao,
                        attendanceLogDao = attendanceLogDao,
                    )
                }
            }
        }

        when (val res = sensorServer.getKeypadOutput(1)) {
            is Result.Error -> {
                logError("Keypad", res.error, res.debugMessage)
                sensorServer.displayText(listOf("Error reading keypad"))
            }

            is Result.Success -> {
                when (res.data) {
                    KeypadResult.Key1 -> handleDisplayIp(sensorServer)

                    KeypadResult.KeyA -> {
                        sensorServer.displayText(listOf("Shutting Down"))
                        delay(2000)
                        sensorServer.displayText(listOf())
                        break
                    }

                    KeypadResult.NoInput -> logInfo("No keypad input.")

                    else -> {
                        logInfo("Invalid key: ${res.data}")
                        sensorServer.displayText(listOf("Invalid key"))
                        delay(1000)
                    }
                }
            }
        }
    }
}

private suspend fun handleDisplayIp(sensorServer: SensorServer) {
    sensorServer.getStatus().onSuccess { status ->
        sensorServer.displayText(listOf("ADMIN SERVER", "${status.ip}:8080"))
        delay(5000)
    }
}

private suspend fun handleFaceEvent(
    event: FaceEvent,
    sensorServer: SensorServer,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
) {
    processAttendance(
        biometricId = event.faculty.id,
        source = "Face",
        sensorServer = sensorServer,
        teacherDao = teacherDao,
        attendanceLogDao = attendanceLogDao,
    )
}

private suspend fun processAttendance(
    biometricId: String,
    source: String,
    sensorServer: SensorServer,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
): Boolean {
    teacherDao.getTeacherByBiometricId(biometricId)?.let { teacher ->
        sensorServer.displayText(listOf("$source Found", teacher.firstName, teacher.subjectTaught))
        logInfo("$source Found Faculty ${teacher.firstName} : ${teacher.subjectTaught}")
        logAttendance(
            biometricId = biometricId,
            entityType = EntityType.TEACHER,
            entityId = teacher.id,
            attendanceLogDao = attendanceLogDao,
        )
        return true
    }

    return false
}

private suspend fun logAttendance(
    biometricId: String,
    entityType: EntityType,
    entityId: Long,
    attendanceLogDao: AttendanceLogDao,
) {
    val pastLogsForToday =
        attendanceLogDao.getAttendanceLogByBiometricId(biometricId).filter {
            it.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).date ==
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        }

    if (
        pastLogsForToday.any {
            Clock.System.now().toEpochMilliseconds() <= it.timeStamp.toEpochMilliseconds() + 30_000
        }
    ) {
        logInfo(
            "$entityType, id:$entityId has been recently logged. skipping logging this instance"
        )
        return
    }

    attendanceLogDao.upsert(
        AttendanceLog(
                biometricId = biometricId,
                entityType = entityType,
                entityId = entityId,
                timeStamp = Clock.System.now(),
                attendanceStatus =
                    if (pastLogsForToday.size % 2 == 0) {
                        AttendanceStatus.IN
                    } else {
                        AttendanceStatus.OUT
                    },
            )
            .toAttendanceLogEntity()
    )
}
