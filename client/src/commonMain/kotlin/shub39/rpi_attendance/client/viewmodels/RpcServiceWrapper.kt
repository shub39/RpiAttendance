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

import AdminInterface
import io.ktor.client.HttpClient
import io.ktor.client.request.url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.withService

class RpcServiceWrapper {
    var rpcService: AdminInterface? = null
    val isInterfaceChecked = MutableStateFlow(false)

    private val client = HttpClient { installKrpc() }
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var rpcCheckJob: Job? = null

    fun setUrl(url: String) {
        rpcService =
            client
                .rpc {
                    url("ws://$url/rpc")
                    rpcConfig { serialization { json { allowStructuredMapKeys = true } } }
                }
                .withService<AdminInterface>()
    }

    fun checkUrl(url: String) {
        rpcCheckJob?.cancel()
        rpcCheckJob =
            scope.launch {
                try {
                    val tempInterface =
                        client
                            .rpc {
                                url("ws://$url/rpc")
                                rpcConfig {
                                    serialization { json { allowStructuredMapKeys = true } }
                                }
                            }
                            .withService<AdminInterface>()
                    isInterfaceChecked.update { tempInterface.getStatus() }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isInterfaceChecked.update { false }
                }
            }
    }
}
