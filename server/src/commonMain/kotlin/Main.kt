import data.SensorServerImpl
import data.database.getDatabaseBuilder
import data.database.getRoomDatabase
import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main() {

    val client = HttpClient(Curl) {
        install(ContentNegotiation) {
            json(
                json = Json { ignoreUnknownKeys = true }
            )
        }

        engine {
            sslVerify = true
            caInfo = "/etc/ssl/certs/ca-certificates.crt"
            caPath = "/etc/ssl/certs"
        }
    }

    val sensorServer = SensorServerImpl(client)
    val db = getRoomDatabase(getDatabaseBuilder())

    runBlocking {
        println("Starting server...")
        println("Displaying Status Message...")

        when (val result = sensorServer.displayText(listOf("Hello", "World"))) {
            is Result.Error -> {
                println("Error: ${result.error}")
                return@runBlocking
            }
            is Result.Success -> {
                println("Displayed Status Message...")
            }
        }

        println("================================")
        println("1. Display Text")
        println("2. Get Status")
        println("3. Enroll Face")
        println("4. Enroll Fingerprint")
        println("5. Search Face")
        println("6. Search Fingerprint")
        println("7. Delete Face")
        println("8. Delete Fingerprint")
        println("================================")


        while (true) {
            println("Enter option:")
            val input = readlnOrNull()?.trim()

            when (input) {

                "1" -> {
                    println("Enter text (space separated):")
                    val text = readlnOrNull()
                    if (text == null) {
                        println("Invalid input")
                        continue
                    }

                    when (val res = sensorServer.displayText(text.split(" "))) {
                        is Result.Success -> println("Text displayed")
                        is Result.Error -> println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "2" -> {
                    when (val res = sensorServer.getStatus()) {
                        is Result.Success -> println("Status OK")
                        is Result.Error -> println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "3" -> {
                    println("Enter name for face enrollment:")
                    val name = readlnOrNull()
                    if (name.isNullOrBlank()) {
                        println("Invalid name")
                        continue
                    }

                    when (val res = sensorServer.enrollFace(name)) {
                        is Result.Success -> println("Face enrolled")
                        is Result.Error -> println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "4" -> {
                    when (val res = sensorServer.enrollFingerPrint()) {
                        is Result.Success ->
                            println("Fingerprint enrolled with ID: ${res.data}")
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "5" -> {
                    when (val res = sensorServer.recognizeFace()) {
                        is Result.Success -> {
                            println(res.data)
                        }
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "6" -> {
                    when (val res = sensorServer.searchFingerPrint()) {
                        is Result.Success -> {
                            println(res.data)
                        }
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "7" -> {
                    println("Enter face ID to delete:")
                    val id = readlnOrNull()
                    if (id.isNullOrBlank()) {
                        println("Invalid ID")
                        continue
                    }

                    when (val res = sensorServer.deleteFace(id)) {
                        is Result.Success -> println("Face deleted")
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "8" -> {
                    println("Enter fingerprint ID to delete:")
                    val id = readlnOrNull()?.toIntOrNull()
                    if (id == null) {
                        println("Invalid ID")
                        continue
                    }

                    when (val res = sensorServer.deleteFingerPrint(id)) {
                        is Result.Success -> println("Fingerprint deleted")
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                "9" -> {
                    println("Enter keypad timeout (seconds):")
                    val timeout = readlnOrNull()?.toIntOrNull()
                    if (timeout == null || timeout <= 0) {
                        println("Invalid timeout")
                        continue
                    }

                    when (val res = sensorServer.getKeypadOutput(timeout)) {
                        is Result.Success -> {
                            println(res.data)
                        }
                        is Result.Error ->
                            println("Error: ${res.error} ${res.debugMessage ?: ""}")
                    }
                }

                else -> println("Invalid option")
            }
        }
    }

}