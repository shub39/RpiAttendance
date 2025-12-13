import io.ktor.client.*
import io.ktor.client.engine.curl.*

fun main() {

    val client = HttpClient(Curl) {
        engine {
            sslVerify = true
            caInfo = "/etc/ssl/certs/ca-certificates.crt"
            caPath = "/etc/ssl/certs"
        }
    }

}