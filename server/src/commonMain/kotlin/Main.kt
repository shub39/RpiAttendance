import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

fun main() {
    println("Hello World!")

    val client = HttpClient(Curl)
    val response = runBlocking {
        client.get("https://ktor.io/")
    }
    println(response.status)
    client.close()
}