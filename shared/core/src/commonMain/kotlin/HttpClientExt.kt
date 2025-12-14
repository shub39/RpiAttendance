import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.statement.*
import io.ktor.util.network.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, SourceError> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(
            SourceError.NetworkError.REQUEST_FAILED,
            "SocketTimeoutException: ${e.message}"
        )
    } catch (e: UnresolvedAddressException) {
        return Result.Error(
            SourceError.NetworkError.NO_INTERNET_CONNECTION,
            "UnresolvedAddressException: ${e.message}"
        )
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(
            SourceError.DataError.UNKNOWN_ERROR,
            "Unexpected exception: ${e::class.simpleName} - ${e.message}\n${e.stackTraceToString().take(500)}"
        )
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, SourceError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(
                    SourceError.DataError.PARSE_ERROR,
                    "Parse error for ${T::class.simpleName}: ${e.message}"
                )
            }
        }

        in 400..499 -> {
            Result.Error(
                SourceError.DataError.SENSOR_ERROR,
                "Bad request: ${response.status.value} - ${response.status.description}"
            )
        }

        else -> Result.Error(
            SourceError.DataError.UNKNOWN_ERROR,
            "HTTP ${response.status.value}: ${response.status.description}\nURL: ${response.request.url}"
        )
    }
}