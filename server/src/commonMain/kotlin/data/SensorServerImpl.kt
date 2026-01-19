package data

import EmptyResult
import Result
import SourceError
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
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import safeCall

class SensorServerImpl(
    private val client: HttpClient
) : SensorServer {

    override suspend fun displayText(lines: List<String>): EmptyResult<SensorError> {
        val request: EmptyResult<SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/display")
            ) {
                contentType(ContentType.Application.Json)
                setBody(DisplayRequest(lines))
            }
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun enrollFace(name: String): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/face/enroll")
            ) {
                contentType(ContentType.Application.Json)
                setBody(FaceEnrollRequest(name))
            }
        }

        return when (request) {
            is Result.Error -> {
                if (request.error == SourceError.DataError.SENSOR_ERROR) {
                    Result.Error(
                        error = SensorError.FACE_TIMEOUT,
                        debugMessage = request.debugMessage
                    )
                } else {
                    Result.Error(
                        error = SensorError.SERVER_ERROR,
                        debugMessage = request.debugMessage
                    )
                }
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun recognizeFace(): Result<FaceSearchResult, SensorError> {
        val request: Result<FaceSearchResponse, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/face/recognize")
            )
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(
                if (request.data.match != null) {
                    FaceSearchResult.Found(request.data.match!!)
                } else {
                    FaceSearchResult.NotFound
                }
            )
        }
    }

    override suspend fun enrollFingerPrint(): Result<Int, SensorError> {
        val request: Result<FingerPrintEnrollResponse, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/fingerprint/enroll")
            )
        }

        return when (request) {
            is Result.Error -> {
                if (request.error == SourceError.DataError.SENSOR_ERROR) {
                    Result.Error(
                        error = SensorError.FINGERPRINT_TIMEOUT,
                        debugMessage = request.debugMessage
                    )
                } else {
                    Result.Error(
                        error = SensorError.SERVER_ERROR,
                        debugMessage = request.debugMessage
                    )
                }
            }

            is Result.Success -> Result.Success(request.data.index)
        }
    }

    override suspend fun searchFingerPrint(): Result<FingerprintSearchResult, SensorError> {
        val request: Result<FingerPrintSearchResponse, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/fingerprint/search")
            )
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(
                if (request.data.index != null) {
                    FingerprintSearchResult.Found(request.data.index!!)
                } else {
                    FingerprintSearchResult.NotFound
                }
            )
        }
    }

    override suspend fun deleteFingerPrint(id: Int): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/fingerprint/delete")
            ) {
                contentType(ContentType.Application.Json)
                setBody(FingerPrintDeleteRequest(id))
            }
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun deleteFace(id: String): EmptyResult<SensorError> {
        val request: Result<Response, SourceError> = safeCall {
            client.post(
                url = Url("$BASE_URL/face/delete")
            ) {
                contentType(ContentType.Application.Json)
                setBody(FaceDeleteRequest(id))
            }
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun getStatus(): Result<StatusResponse, SensorError> {
        val request: Result<StatusResponse, SourceError> = safeCall {
            client.get(
                url = Url("$BASE_URL/status")
            ) {
                contentType(ContentType.Application.Json)
            }
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
            }

            is Result.Success -> Result.Success(request.data)
        }
    }

    override suspend fun getKeypadOutput(timeout: Int): Result<KeypadResult, SensorError> {
        val request: Result<KeypadResponse, SourceError> = safeCall {
            client.get(
                url = Url("$BASE_URL/keypad")
            ) {
                parameter("timeout", timeout)
            }
        }

        return when (request) {
            is Result.Error -> {
                Result.Error(
                    error = SensorError.SERVER_ERROR,
                    debugMessage = request.debugMessage
                )
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