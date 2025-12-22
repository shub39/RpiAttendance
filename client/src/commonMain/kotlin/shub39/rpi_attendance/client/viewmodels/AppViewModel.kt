package shub39.rpi_attendance.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class AppViewModel(
    private val rpcServiceWrapper: RpcServiceWrapper
): ViewModel() {
    val isInterfaceChecked = rpcServiceWrapper.isInterfaceChecked.asStateFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun checkUrl(url: String) = rpcServiceWrapper.checkUrl(url)
    fun setUrl(url: String) = rpcServiceWrapper.setUrl(url)
}