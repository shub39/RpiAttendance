package shub39.rpi_attendance.client.viewmodels

import EnrollState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import models.Student
import shub39.rpi_attendance.client.presentation.students_screen.StudentsScreenAction
import shub39.rpi_attendance.client.presentation.students_screen.StudentsScreenState

class StudentsScreenViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
) : ViewModel() {
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
            is StudentsScreenAction.UpsertStudent -> viewModelScope.launch {
                rpcServiceWrapper.rpcService?.upsertStudent(action.student)
            }

            is StudentsScreenAction.DeleteStudent -> viewModelScope.launch {
                rpcServiceWrapper.rpcService?.deleteStudent(action.student)
            }

            is StudentsScreenAction.EnrollStudent -> viewModelScope.launch {
                rpcServiceWrapper.rpcService
                    ?.addBiometricDetailsForStudent(action.student)
                    ?.onEach { enrollState ->
                        _state.update {
                            it.copy(
                                enrollState = enrollState
                            )
                        }
                    }
                    ?.launchIn(this)
            }

            is StudentsScreenAction.OnChangeSearchQuery -> {
                if (action.query.isBlank()) {
                    _state.update {
                        it.copy(searchQuery = action.query, searchResults = emptyList())
                    }
                    return
                }

                _state.update { studentsScreenState ->
                    studentsScreenState.copy(
                        searchQuery = action.query,
                        searchResults = studentsScreenState.students.filter {
                            it.firstName.contains(action.query, ignoreCase = true) ||
                                    it.lastName.contains(action.query, ignoreCase = true) ||
                                    it.rollNo.toString().contains(action.query)
                        }
                    )
                }
            }

            StudentsScreenAction.ResetEnrollState -> {
                _state.update {
                    it.copy(enrollState = EnrollState.Idle)
                }
            }

            is StudentsScreenAction.ImportList -> viewModelScope.launch {
                try {
                    val rawFile = action.file.readString()
                    val students = Json.decodeFromString<List<Student>>(rawFile)

                    students.forEach { student ->
                        rpcServiceWrapper.rpcService?.upsertStudent(student)
                    }
                } catch (e: SerializationException) {
                    println("Error parsing JSON: ${e.message}")
                }
            }
        }
    }

    private fun onStartSync() {
        dataSyncJob?.cancel()
        dataSyncJob = viewModelScope.launch {
            rpcServiceWrapper.rpcService?.getStudents()?.onEach { students ->
                _state.update {
                    it.copy(students = students)
                }
            }?.launchIn(this)
        }
    }
}