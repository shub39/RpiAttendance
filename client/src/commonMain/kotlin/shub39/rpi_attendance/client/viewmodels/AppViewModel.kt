package shub39.rpi_attendance.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper,
    private val stateLayer: StateLayer
): ViewModel() {

    private var dataSyncJob: Job? = null

    val isInterfaceChecked = rpcServiceWrapper.isInterfaceChecked.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun checkUrl(url: String) = rpcServiceWrapper.checkUrl(url)
    fun setUrl(url: String) {
        rpcServiceWrapper.setUrl(url)

        dataSyncJob?.cancel()
        dataSyncJob = viewModelScope.launch {
            rpcServiceWrapper.rpcService?.let { adminInterface ->
                combine(
                    adminInterface.getStudents(),
                    adminInterface.getTeachers(),
                    adminInterface.getCourses(),
                    adminInterface.getAttendanceLogs()
                ) { students, teachers, courses, attendanceLogs ->
                    stateLayer.database.update {
                        it.copy(
                            students = students,
                            teachers = teachers,
                            courses = courses,
                            attendanceLogs = attendanceLogs
                        )
                    }
                }.launchIn(this)
            }
        }
    }
}