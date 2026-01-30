package shub39.rpi_attendance.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import shub39.rpi_attendance.client.presentation.attendancelog_screen.AttendanceLogAction
import shub39.rpi_attendance.client.presentation.attendancelog_screen.AttendanceLogState

class AttendanceLogScreenViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
) : ViewModel() {
    private var observeJob: Job? = null

    private val _state = MutableStateFlow(AttendanceLogState())
    val state = _state.asStateFlow()
        .onStart { observeLogs() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceLogState()
        )

    fun onAction(action: AttendanceLogAction) {
        when (action) {
            is AttendanceLogAction.OnDeleteLog -> viewModelScope.launch {
                rpcServiceWrapper.rpcService?.deleteAttendanceLog(action.log)
            }

            is AttendanceLogAction.OnLoadDate -> {
                _state.update { it.copy(selectedDate = action.date) }
                observeLogs()
            }
        }
    }

    private fun observeLogs() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            rpcServiceWrapper.rpcService
                ?.getDetailedAttendanceLogs()
                ?.onEach { logs ->
                    _state.update { attendanceLogState ->
                        attendanceLogState.copy(
                            allDetailedLogs = logs,
                            filteredDetailedLogs = logs.filter {
                                it.log.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).date == state.value.selectedDate
                            }
                        )
                    }
                }
                ?.launchIn(this)
        }
    }
}