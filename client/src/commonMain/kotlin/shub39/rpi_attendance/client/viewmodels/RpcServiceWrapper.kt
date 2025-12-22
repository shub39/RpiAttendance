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
    }

    fun checkUrl(url: String) {
        println(url)
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