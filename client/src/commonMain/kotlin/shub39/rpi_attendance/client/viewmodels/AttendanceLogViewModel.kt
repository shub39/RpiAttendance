package shub39.rpi_attendance.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import shub39.rpi_attendance.client.presentation.attendancelog_screen.AttendanceLogAction
import shub39.rpi_attendance.client.presentation.attendancelog_screen.AttendanceLogState

class AttendanceLogViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
): ViewModel() {
    private val _state = MutableStateFlow(AttendanceLogState())
    val state = _state.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AttendanceLogState()
        )

    fun onAction(action: AttendanceLogAction) {
        when (action) {
            is AttendanceLogAction.OnGetSessions -> viewModelScope.launch {
                _state.update {
                    it.copy(
                        selectedDate = action.date,
                        sessions = rpcServiceWrapper.rpcService?.getSessionsForDate(action.date) ?: emptyList()
                    )
                }
            }
        }
    }
}