import data.database.AttendanceLogDao
import data.database.StudentDao
import data.database.TeacherDao
import domain.FaceSearchResult
import domain.FingerprintSearchResult
import domain.KeypadResult
import domain.SensorServer
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun mainLoop(
    studentDao: StudentDao,
    teacherDao: TeacherDao,
    attendanceLogDao: AttendanceLogDao,
    sensorServer: SensorServer,
    adminServer: AdminServer,
    client: HttpClient
) {
    runBlocking {
        sensorServer.displayText(listOf(
            "Rpiattendance",
            "by shub39"
        ))
        delay(2000)

        while (true) {
            sensorServer.displayText(listOf(
                "select option",
                "1. display ip",
                "4. attendance"
            ))

            when (val res = sensorServer.getKeypadOutput(10)) {
                is Result.Error -> {
                    println("Error Reading Keypad: ${res.error} ${res.debugMessage ?: ""}")
                    sensorServer.displayText(listOf("Error reading keypad"))
                }
                is Result.Success -> {
                    when (res.data) {
                        KeypadResult.Key1 -> {
                            sensorServer.getStatus().onSuccess { status ->
                                sensorServer.displayText(listOf(
                                    "ADMIN SERVER",
                                    "${status.ip}:8080"
                                ))
                                delay(5000)
                            }
                        }
                        KeypadResult.Key4 -> {
                            sensorServer.displayText(listOf("Taking", "attendance"))
                            val faceSearch = launch {
                                when (val face = sensorServer.recognizeFace()) {
                                    is Result.Error -> {
                                        println("Error : ${face.error} ${face.debugMessage ?: ""}")
                                    }
                                    is Result.Success -> {
                                        when (face.data) {
                                            is FaceSearchResult.Found -> {
                                                val bioId = (face.data as FaceSearchResult.Found).name

                                                studentDao.getStudentByBiometricId(bioId)?.let { student ->
                                                    sensorServer.displayText(listOf(
                                                        "Face Found",
                                                        student.firstName,
                                                        student.rollNo.toString()
                                                    ))
                                                } ?: teacherDao.getTeacherByBiometricId(bioId)?.let { teacher ->
                                                    sensorServer.displayText(listOf(
                                                        "Face Found",
                                                        teacher.firstName,
                                                        teacher.subjectTaught
                                                    ))
                                                }

                                                delay(1000)
                                            }
                                            FaceSearchResult.NotFound -> {
                                                println("Face not found")
                                            }
                                        }
                                    }
                                }
                            }
                            val fingerprintSearch = launch {
                                when (val finger = sensorServer.searchFingerPrint()) {
                                    is Result.Error -> {
                                        println("Error : ${finger.error} ${finger.debugMessage ?: ""}")
                                    }
                                    is Result.Success -> {
                                        when (finger.data) {
                                            is FingerprintSearchResult.Found -> {
                                                val bioId = (finger.data as FingerprintSearchResult.Found).id

                                                studentDao.getStudentByBiometricId(bioId.toString())?.let { student ->
                                                    sensorServer.displayText(listOf(
                                                        "Fingerprint Found",
                                                        student.firstName,
                                                        student.rollNo.toString()
                                                    ))
                                                } ?: teacherDao.getTeacherByBiometricId(bioId.toString())?.let { teacher ->
                                                    sensorServer.displayText(listOf(
                                                        "Fingerprint Found",
                                                        teacher.firstName,
                                                        teacher.subjectTaught
                                                    ))
                                                }

                                                delay(1000)
                                            }
                                            FingerprintSearchResult.NotFound -> {
                                                println("Finger not found")
                                            }
                                        }
                                    }
                                }
                            }

                            faceSearch.join()
                            fingerprintSearch.join()
                        }
                        KeypadResult.KeyA -> {
                            sensorServer.displayText(listOf("Shutting Down"))
                            delay(2000)
                            sensorServer.displayText(listOf())

                            adminServer.stop(1000, 2000)
                            client.close()
                            println("Server stopped.")

                            break
                        }
                        KeypadResult.NoInput -> {
                            println("No Input")
                        }
                        else -> {
                            println("Invalid key: ${res.data}")
                            sensorServer.displayText(listOf("Invalid key"))
                            delay(1000)
                        }
                    }
                }
            }
        }
    }
}