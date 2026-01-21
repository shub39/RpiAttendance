import domain.KeypadResult
import domain.SensorServer
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun mainLoop(
    sensorServer: SensorServer,
    adminServer: AdminServer,
    client: HttpClient
) {
    runBlocking {
        sensorServer.displayText(listOf(
            "Rpiattendance",
            "by shub39"
        ))
        delay(2000)

        while (true) {
            sensorServer.displayText(listOf(
                "select option",
                "1. display ip",
                "4. attendance"
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
                        KeypadResult.Key4 -> {
                            println("Key 4 pressed")
                        }
                        KeypadResult.KeyA -> {
                            sensorServer.displayText(listOf("Shutting Down"))
                            delay(2000)
                            sensorServer.displayText(listOf())

                            adminServer.stop(1000, 2000)
                            client.close()
                            println("Server stopped.")

                            break
                        }
                        KeypadResult.NoInput -> {
                            println("No Input")
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