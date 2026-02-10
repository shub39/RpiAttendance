import data.database.AttendanceLogDao
import data.database.StudentDao
import data.database.TeacherDao
import data.toAttendanceLogEntity
import domain.FaceSearchResult
import domain.FingerprintSearchResult
import domain.KeypadResult
import domain.SensorServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AttendanceLog
import models.AttendanceStatus
import models.EntityType
import kotlin.time.Clock

suspend fun mainLoop(
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    sensorServer: SensorServer,
) {
    sensorServer.displayText(listOf("Rpiattendance", "by shub39"))
    delay(2000)

    while (true) {
        displayMenu(sensorServer)

        when (val res = sensorServer.getKeypadOutput(10)) {
            is Result.Error -> {
                logError("Keypad", res.error, res.debugMessage)
                sensorServer.displayText(listOf("Error reading keypad"))
            }

            is Result.Success -> {
                when (res.data) {
                    KeypadResult.Key1 -> handleDisplayIp(sensorServer)

                    KeypadResult.Key4 -> takeAttendance(
                        sensorServer = sensorServer,
                        studentDao = studentDao,
                        teacherDao = teacherDao,
                        attendanceLogDao = attendanceLogDao,
                    )

                    KeypadResult.Key7 -> takeAttendance(
                        sensorServer = sensorServer,
                        studentDao = studentDao,
                        teacherDao = teacherDao,
                        attendanceLogDao = attendanceLogDao,
                        isBulk = true
                    )

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

private suspend fun displayMenu(sensorServer: SensorServer) {
    sensorServer.displayText(
        listOf(
            "1. display ip",
            "4. attendance",
            "7. bulk attendance"
        )
    )
}

private suspend fun handleDisplayIp(sensorServer: SensorServer) {
    sensorServer.getStatus().onSuccess { status ->
        sensorServer.displayText(
            listOf(
                "ADMIN SERVER",
                "${status.ip}:8080"
            )
        )
        delay(5000)
    }
}

private suspend fun takeAttendance(
    sensorServer: SensorServer,
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    isBulk: Boolean = false
) {
    sensorServer.updateSensorsBusyState(true)

    val repetitions = if (isBulk) 30 else 5
    sensorServer.displayText(
        if (isBulk) {
            listOf("Taking Bulk", "Attendance")
        } else {
            listOf("Taking", "Attendance")
        }
    )

    coroutineScope {
        launch {
            repeat(repetitions) {
                handleFaceRecognition(
                    sensorServer = sensorServer,
                    studentDao = studentDao,
                    teacherDao = teacherDao,
                    attendanceLogDao = attendanceLogDao
                )
            }
        }
        launch {
            repeat(repetitions) {
                handleFingerprintSearch(
                    sensorServer = sensorServer,
                    studentDao = studentDao,
                    teacherDao = teacherDao,
                    attendanceLogDao = attendanceLogDao
                )
            }
        }
    }

    sensorServer.updateSensorsBusyState(false)
}

private suspend fun handleFaceRecognition(
    sensorServer: SensorServer,
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao
) {
    when (val face = sensorServer.recognizeFace()) {
        is Result.Error -> logError("Face Recognition", face.error, face.debugMessage)
        is Result.Success -> {
            when (val faceData = face.data) {
                is FaceSearchResult.Found -> {
                    if (
                        processAttendance(
                            biometricId = faceData.name,
                            source = "Face",
                            sensorServer = sensorServer,
                            studentDao = studentDao,
                            teacherDao = teacherDao,
                            attendanceLogDao = attendanceLogDao
                        )
                    ) {
                        delay(1000)
                    }
                }

                FaceSearchResult.NotFound -> logInfo("Face not found")
            }
        }
    }
}

private suspend fun handleFingerprintSearch(
    sensorServer: SensorServer,
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao
) {
    when (val finger = sensorServer.searchFingerPrint()) {
        is Result.Error -> logError("Fingerprint Search", finger.error, finger.debugMessage)
        is Result.Success -> {
            when (val fingerData = finger.data) {
                is FingerprintSearchResult.Found -> {
                    if (
                        processAttendance(
                            biometricId = fingerData.id.toString(),
                            source = "Fingerprint",
                            sensorServer = sensorServer,
                            studentDao = studentDao,
                            teacherDao = teacherDao,
                            attendanceLogDao = attendanceLogDao
                        )
                    ) {
                        delay(1000)
                    }
                }

                FingerprintSearchResult.NotFound -> logInfo("Fingerprint not found")
            }
        }
    }
}

private suspend fun processAttendance(
    biometricId: String,
    source: String,
    sensorServer: SensorServer,
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao
): Boolean {
    studentDao.getStudentByBiometricId(biometricId)?.let { student ->
        sensorServer.displayText(
            listOf(
                "$source Found",
                student.firstName,
                student.rollNo.toString()
            )
        )
        logInfo("$source Found Student ${student.firstName} : ${student.rollNo}")
        logAttendance(
            biometricId = biometricId,
            entityType = EntityType.STUDENT,
            entityId = student.id,
            attendanceLogDao = attendanceLogDao
        )
        return true
    }

    teacherDao.getTeacherByBiometricId(biometricId)?.let { teacher ->
        sensorServer.displayText(
            listOf(
                "$source Found",
                teacher.firstName,
                teacher.subjectTaught
            )
        )
        logInfo("$source Found Student ${teacher.firstName} : ${teacher.subjectTaught}")
        logAttendance(
            biometricId = biometricId,
            entityType = EntityType.TEACHER,
            entityId = teacher.id,
            attendanceLogDao = attendanceLogDao
        )
        return true
    }

    return false
}

private suspend fun logAttendance(
    biometricId: String,
    entityType: EntityType,
    entityId: Long,
    attendanceLogDao: AttendanceLogDao
) {
    val pastLogsForToday = attendanceLogDao.getAttendanceLogByBiometricId(biometricId).filter {
        it.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).date ==
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    attendanceLogDao.upsert(
        AttendanceLog(
            biometricId = biometricId,
            entityType = entityType,
            entityId = entityId,
            timeStamp = Clock.System.now(),
            attendanceStatus = if (pastLogsForToday.size % 2 == 0) {
                AttendanceStatus.IN
            } else {
                AttendanceStatus.OUT
            }
        ).toAttendanceLogEntity()
    )
}