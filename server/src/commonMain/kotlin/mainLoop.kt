import domain.KeypadResult
import domain.SensorServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun mainLoop(
    sensorServer: SensorServer
) {
    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch {
        while (true) {
            sensorServer.displayText(listOf(
                "Rpiattendance",
                "by shub39"
            ))
            delay(2000)

            sensorServer.displayText(listOf(
                "select option",
                "1. display ip",
                "2. attendance"
            ))

            when (val res = sensorServer.getKeypadOutput(10)) {
                is Result.Error -> {
                    println("Error Reading Keypad: ${res.error} ${res.debugMessage ?: ""}")
                    sensorServer.displayText(listOf("Error reading keypad"))
                }
                is Result.Success -> {
                    when (res.data) {
                        KeypadResult.Key1 -> {
                            sensorServer.getStatus().onSuccess { status ->
                                sensorServer.displayText(listOf(
                                    "ADMIN SERVER",
                                    "${status.ip}:8080"
                                ))
                                delay(5000)
                            }
                        }
                        KeypadResult.Key2 -> {

                        }
                        else -> {
                            println("Invalid key: ${res.data}")
                            sensorServer.displayText(listOf("Invalid key"))
                            delay(1000)
                        }
                    }
                }
            }
        }
    }
}