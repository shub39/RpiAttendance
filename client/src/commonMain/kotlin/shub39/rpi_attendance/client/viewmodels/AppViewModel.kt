/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import shub39.rpi_attendance.client.domain.AppDatastore

class AppViewModel(
    private val appDatastore: AppDatastore,
    private val rpcServiceWrapper: RpcServiceWrapper,
) : ViewModel() {
    private var syncJob: Job? = null

    val serverUrl = MutableStateFlow("")
    val isInterfaceChecked =
        rpcServiceWrapper.isInterfaceChecked
            .asStateFlow()
            .onStart { startSync() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = false,
            )

    fun updateServerUrl(url: String) {
        serverUrl.update { url }
        rpcServiceWrapper.checkUrl(url)

        if (isInterfaceChecked.value) {
            saveUrl(url)
        }
    }

    fun saveUrl(url: String) {
        rpcServiceWrapper.setUrl(url)
        viewModelScope.launch { appDatastore.setServerUrl(url) }
    }

    private fun startSync() {
        syncJob?.cancel()
        syncJob =
            viewModelScope.launch {
                appDatastore
                    .getServerUrl()
                    .onEach { url ->
                        serverUrl.update { url }
                        rpcServiceWrapper.checkUrl(url)
                    }
                    .launchIn(this)
            }
    }
}
