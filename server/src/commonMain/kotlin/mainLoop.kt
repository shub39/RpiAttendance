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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AttendanceLog
import models.AttendanceStatus
import models.EntityType
import kotlin.time.Clock

val sensorMutex = Mutex()

suspend fun mainLoop(
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    sensorServer: SensorServer,
    client: HttpClient
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
                        sensorServer,
                        studentDao,
                        teacherDao,
                        attendanceLogDao,
                        10_000
                    )

                    KeypadResult.Key7 -> takeAttendance(
                        sensorServer,
                        studentDao,
                        teacherDao,
                        attendanceLogDao,
                        30_000
                    )

                    KeypadResult.KeyA -> {
                        handleShutdown(sensorServer, client)
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
    timeout: Long
) {
    sensorServer.displayText(listOf("Taking bulk", "attendance"))

    withTimeoutOrNull(timeout) {
        coroutineScope {
            launch {
                while (isActive) {
                    sensorMutex.withLock {
                        handleFaceRecognition(
                            sensorServer,
                            studentDao,
                            teacherDao,
                            attendanceLogDao
                        )
                    }
                    delay(50)
                    yield()
                }
            }
            launch {
                while (isActive) {
                    sensorMutex.withLock {
                        handleFingerprintSearch(
                            sensorServer,
                            studentDao,
                            teacherDao,
                            attendanceLogDao
                        )
                    }
                    delay(50)
                    yield()
                }
            }
        }
    }
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
                    if (processAttendance(
                            faceData.name,
                            "Face",
                            sensorServer,
                            studentDao,
                            teacherDao,
                            attendanceLogDao
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

private suspend fun handleShutdown(
    sensorServer: SensorServer,
    client: HttpClient
) {
    sensorServer.displayText(listOf("Shutting Down"))
    delay(2000)
    sensorServer.displayText(listOf())
    client.close()
    logInfo("Server stopped.")
}