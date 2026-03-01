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
import errors.Result
import errors.SourceError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, SourceError> {
    val response =
        try {
            execute()
        } catch (e: SocketTimeoutException) {
            return Result.Error(
                SourceError.NetworkError.REQUEST_FAILED,
                "SocketTimeoutException: ${e.message}",
            )
        } catch (e: UnresolvedAddressException) {
            return Result.Error(
                SourceError.NetworkError.NO_INTERNET_CONNECTION,
                "UnresolvedAddressException: ${e.message}",
            )
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            return Result.Error(
                SourceError.DataError.UNKNOWN_ERROR,
                "Unexpected exception: ${e::class.simpleName} - ${e.message}\n${e.stackTraceToString().take(500)}",
            )
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, SourceError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(
                    SourceError.DataError.PARSE_ERROR,
                    "Parse error for ${T::class.simpleName}: ${e.message}",
                )
            }
        }

        in 400..499 -> {
            Result.Error(
                SourceError.DataError.SENSOR_ERROR,
                "Bad request: ${response.status.value} - ${response.status.description}",
            )
        }

        else ->
            Result.Error(
                SourceError.DataError.UNKNOWN_ERROR,
                "HTTP ${response.status.value}: ${response.status.description}\nURL: ${response.request.url}",
            )
    }
}
