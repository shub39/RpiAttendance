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
package data

import domain.DisplayRequest
import domain.FaceDeleteRequest
import domain.FaceEnrollRequest
import domain.FaceSearchResponse
import domain.FaceSearchResult
import domain.FingerPrintDeleteRequest
import domain.FingerPrintEnrollResponse
import domain.FingerPrintSearchResponse
import domain.FingerprintSearchResult
import domain.KeypadResponse
import domain.KeypadResult
import domain.Response
import domain.SensorError
import domain.SensorServer
import domain.StatusResponse
import domain.toKeypadResult
import errors.EmptyResult
import errors.Result
import errors.SourceError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import safeCall

class SensorServerImpl(private val client: HttpClient) : SensorServer {

    private val mutex = Mutex()
    private val _areSensorsBusy: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isAdminOperationActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val isAdminOperationActive: Flow<Boolean> = _isAdminOperationActive.asStateFlow()

    override fun updateAdminOperationStatus(status: Boolean) {
        _isAdminOperationActive.update { status }
    }

    override val areSensorsBusy: Flow<Boolean> = _areSensorsBusy.asStateFlow()

    override fun updateSensorsBusyState(state: Boolean) {
        _areSensorsBusy.update { state }
    }

    override suspend fun displayText(lines: List<String>): EmptyResult<SensorError> {
        val request: EmptyResult<SourceError> =
            mutex.withLock {
                safeCall {
                    client.post(url = Url("$BASE_URL/display")) {
                        contentType(ContentType.Application.Json)
                        setBody(DisplayRequest(lines))
                    }
                }
            }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun enrollFace(name: String): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> =
            mutex.withLock {
                safeCall {
                    client.post(url = Url("$BASE_URL/face/enroll")) {
                        contentType(ContentType.Application.Json)
                        setBody(FaceEnrollRequest(name))
                    }
                }
            }

        return when (request) {
            is Result.Error -> {
                if (request.error == SourceError.DataError.SENSOR_ERROR) {
                    Result.Error(
                        error = SensorError.FACE_TIMEOUT,
                        debugMessage = request.debugMessage,
                    )
                } else {
                    Result.Error(
                        error = SensorError.SERVER_ERROR,
                        debugMessage = request.debugMessage,
                    )
                }
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun recognizeFace(): Result<FaceSearchResult, SensorError> {
        val request: Result<FaceSearchResponse, SourceError> =
            mutex.withLock { safeCall { client.post(url = Url("$BASE_URL/face/recognize")) } }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success ->
                Result.Success(
                    if (request.data.match != null) {
                        FaceSearchResult.Found(request.data.match!!)
                    } else {
                        FaceSearchResult.NotFound
                    }
                )
        }
    }

    override suspend fun enrollFingerPrint(): Result<Int, SensorError> {
        val request: Result<FingerPrintEnrollResponse, SourceError> =
            mutex.withLock { safeCall { client.post(url = Url("$BASE_URL/fingerprint/enroll")) } }

        return when (request) {
            is Result.Error -> {
                if (request.error == SourceError.DataError.SENSOR_ERROR) {
                    Result.Error(
                        error = SensorError.FINGERPRINT_TIMEOUT,
                        debugMessage = request.debugMessage,
                    )
                } else {
                    Result.Error(
                        error = SensorError.SERVER_ERROR,
                        debugMessage = request.debugMessage,
                    )
                }
            }

            is Result.Success -> Result.Success(request.data.index)
        }
    }

    override suspend fun searchFingerPrint(): Result<FingerprintSearchResult, SensorError> {
        val request: Result<FingerPrintSearchResponse, SourceError> =
            mutex.withLock { safeCall { client.post(url = Url("$BASE_URL/fingerprint/search")) } }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success ->
                Result.Success(
                    if (request.data.index != null) {
                        FingerprintSearchResult.Found(request.data.index!!)
                    } else {
                        FingerprintSearchResult.NotFound
                    }
                )
        }
    }

    override suspend fun deleteFingerPrint(id: Int): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> =
            mutex.withLock {
                safeCall {
                    client.post(url = Url("$BASE_URL/fingerprint/delete")) {
                        contentType(ContentType.Application.Json)
                        setBody(FingerPrintDeleteRequest(id))
                    }
                }
            }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun deleteFace(id: String): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> =
            mutex.withLock {
                safeCall {
                    client.post(url = Url("$BASE_URL/face/delete")) {
                        contentType(ContentType.Application.Json)
                        setBody(FaceDeleteRequest(id))
                    }
                }
            }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun getStatus(): Result<StatusResponse, SensorError> {
        val request: Result<StatusResponse, SourceError> =
            mutex.withLock {
                safeCall {
                    client.get(url = Url("$BASE_URL/status")) {
                        contentType(ContentType.Application.Json)
                    }
                }
            }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success -> Result.Success(request.data)
        }
    }

    override suspend fun getKeypadOutput(timeout: Int): Result<KeypadResult, SensorError> {
        val request: Result<KeypadResponse, SourceError> =
            mutex.withLock {
                safeCall {
                    client.get(url = Url("$BASE_URL/keypad")) { parameter("timeout", timeout) }
                }
            }

        return when (request) {
            is Result.Error -> {
                Result.Error(error = SensorError.SERVER_ERROR, debugMessage = request.debugMessage)
            }

            is Result.Success -> {
                Result.Success(request.data.key.toKeypadResult())
            }
        }
    }

    companion object {
        private const val BASE_URL = "http://localhost:8000"
    }
}
