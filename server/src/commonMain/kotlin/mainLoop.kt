import data.database.AttendanceLogDao
import data.database.StudentDao
import data.database.TeacherDao
import data.toAttendanceLogEntity
import domain.FaceSearchResult
import domain.FingerprintSearchResult
import domain.KeypadResult
import domain.SensorServer
import io.ktor.client.HttpClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AttendanceLog
import models.AttendanceStatus
import models.EntityType
import kotlin.time.Clock

fun mainLoop(
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    sensorServer: SensorServer,
    adminServer: AdminServer,
    client: HttpClient
) {
    runBlocking {
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
                        KeypadResult.Key4 -> takeAttendance(sensorServer, studentDao, teacherDao, attendanceLogDao)
                        KeypadResult.KeyA -> {
                            handleShutdown(sensorServer, adminServer, client)
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
}

private suspend fun displayMenu(sensorServer: SensorServer) {
    sensorServer.displayText(
        listOf(
            "select option",
            "1. display ip",
            "4. attendance"
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
    attendanceLogDao: AttendanceLogDao
) {
    sensorServer.displayText(listOf("Taking", "attendance"))
    coroutineScope {
        launch { handleFaceRecognition(sensorServer, studentDao, teacherDao, attendanceLogDao) }
        launch { handleFingerprintSearch(sensorServer, studentDao, teacherDao, attendanceLogDao) }
    }.join()
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
                    if (processAttendance(faceData.name, "Face", sensorServer, studentDao, teacherDao, attendanceLogDao)) {
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
                    if (processAttendance(
                            fingerData.id.toString(),
                            "Fingerprint",
                            sensorServer,
                            studentDao,
                            teacherDao,
                            attendanceLogDao
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
        logAttendance(biometricId, EntityType.STUDENT, student.id, attendanceLogDao)
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
        logAttendance(biometricId, EntityType.TEACHER, teacher.id, attendanceLogDao)
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

private suspend fun handleShutdown(sensorServer: SensorServer, adminServer: AdminServer, client: HttpClient) {
    sensorServer.displayText(listOf("Shutting Down"))
    delay(2000)
    sensorServer.displayText(listOf())
    adminServer.stop(1000, 2000)
    client.close()
    logInfo("Server stopped.")
}