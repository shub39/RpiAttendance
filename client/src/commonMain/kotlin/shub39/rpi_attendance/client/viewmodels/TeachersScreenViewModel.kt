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
import models.Teacher
import shub39.rpi_attendance.client.presentation.teachers_screen.TeachersScreenAction
import shub39.rpi_attendance.client.presentation.teachers_screen.TeachersScreenState

class TeachersScreenViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
) : ViewModel() {
    private var dataSyncJob: Job? = null

    private val _state = MutableStateFlow(TeachersScreenState())
    val state = _state.asStateFlow()
        .onStart { onStartSync() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _state.value
        )

    fun onAction(action: TeachersScreenAction) {
        when (action) {
            is TeachersScreenAction.UpsertTeacher -> viewModelScope.launch {
                rpcServiceWrapper.rpcService?.upsertTeacher(action.teacher)
            }

            is TeachersScreenAction.DeleteTeacher -> viewModelScope.launch {
                rpcServiceWrapper.rpcService?.deleteTeacher(action.teacher)
            }

            is TeachersScreenAction.EnrollTeacher -> viewModelScope.launch {
                rpcServiceWrapper.rpcService
                    ?.addBiometricDetailsForTeacher(action.teacher)
                    ?.onEach { enrollState ->
                        _state.update {
                            it.copy(
                                enrollState = enrollState
                            )
                        }
                    }
                    ?.launchIn(this)
            }

            is TeachersScreenAction.OnChangeSearchQuery -> {
                if (action.query.isBlank()) {
                    _state.update {
                        it.copy(searchQuery = action.query, searchResults = emptyList())
                    }
                    return
                }

                _state.update { teachersScreenState ->
                    teachersScreenState.copy(
                        searchQuery = action.query,
                        searchResults = teachersScreenState.teachers.filter {
                            it.firstName.contains(action.query, ignoreCase = true) ||
                                    it.lastName.contains(action.query, ignoreCase = true) ||
                                    it.subjectTaught.contains(action.query, ignoreCase = true)
                        }
                    )
                }
            }

            TeachersScreenAction.ResetEnrollState -> {
                _state.update {
                    it.copy(enrollState = EnrollState.Idle)
                }
            }

            is TeachersScreenAction.ImportList -> viewModelScope.launch {
                try {
                    val rawFile = action.file.readString()
                    val students = Json.decodeFromString<List<Teacher>>(rawFile)

                    students.forEach { student ->
                        rpcServiceWrapper.rpcService?.upsertTeacher(student)
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
            rpcServiceWrapper.rpcService?.getTeachers()?.onEach { teachers ->
                _state.update {
                    it.copy(teachers = teachers)
                }
            }?.launchIn(this)
        }
    }
}