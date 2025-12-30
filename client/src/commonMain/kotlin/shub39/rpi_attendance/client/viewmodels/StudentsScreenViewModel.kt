package shub39.rpi_attendance.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shub39.rpi_attendance.client.screens.students_screen.StudentsScreenAction
import shub39.rpi_attendance.client.screens.students_screen.StudentsScreenState

class StudentsScreenViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
): ViewModel() {
    private var dataSyncJob: Job? = null

    private val _state = MutableStateFlow(StudentsScreenState())
    val state = _state.asStateFlow()
        .onStart { onStartSync() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: StudentsScreenAction) {
        when (action) {
            else -> {}
        }
    }

    private fun onStartSync() {
        dataSyncJob?.cancel()
        dataSyncJob = viewModelScope.launch {
            rpcServiceWrapper.rpcService?.let { adminInterface ->
                combine(
                    adminInterface.getStudents(),
                    adminInterface.getCourses()
                ) { students, courses ->
                    val studentsByCourses = courses.map { course ->
                        course to students.filter { it.courseId == course.id }
                    }

                    _state.update {
                        it.copy(studentsByCourses = studentsByCourses)
                    }
                }.launchIn(this)
            }
        }
    }
}