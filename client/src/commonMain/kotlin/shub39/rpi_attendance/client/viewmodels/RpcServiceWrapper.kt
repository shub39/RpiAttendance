package shub39.rpi_attendance.client.viewmodels

import AdminInterface
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

class RpcServiceWrapper {
    var rpcService: AdminInterface? = null
    val isInterfaceChecked = MutableStateFlow(false)
    val models = MutableStateFlow(Models())

    private val client = HttpClient { installKrpc() }
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var rpcCheckJob: Job? = null
    private var dataSyncJob: Job? = null

    fun setUrl(url: String) {
        rpcService = client.rpc {
            url("ws://$url/rpc")
            rpcConfig {
                serialization {
                    json {
                        allowStructuredMapKeys = true
                    }
                }
            }
        }.withService<AdminInterface>()

        rpcService?.let { adminInterface ->
            dataSyncJob?.cancel()
            dataSyncJob = scope.launch {
                combine(
                    adminInterface.getStudents(),
                    adminInterface.getTeachers(),
                    adminInterface.getCourses(),
                    adminInterface.getAttendanceLogs()
                ) { students, teachers, courses, attendanceLogs ->
                    val studentsByCourses = courses.map { course ->
                        course to students.filter { it.courseId == course.id }
                    }
                    val attendanceLogsByDates = attendanceLogs.groupBy {
                        it.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    }.toList()

                    models.update {
                        it.copy(
                            students = students,
                            teachers = teachers,
                            courses = courses,
                            attendanceLogs = attendanceLogs,
                            studentsByCourses = studentsByCourses,
                            attendanceLogsByDates = attendanceLogsByDates
                        )
                    }
                }.launchIn(this)
            }
        }
    }

    fun checkUrl(url: String) {
        rpcCheckJob?.cancel()
        rpcCheckJob = scope.launch {
            try {
                val tempInterface = client.rpc {
                    url("ws://$url/rpc")
                    rpcConfig {
                        serialization {
                            json {
                                allowStructuredMapKeys = true
                            }
                        }
                    }
                }.withService<AdminInterface>()
                isInterfaceChecked.update { tempInterface.getStatus() }
            } catch (e: Exception) {
                e.printStackTrace()
                isInterfaceChecked.update { false }
            }
        }
    }
}