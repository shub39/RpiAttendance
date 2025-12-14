package domain

import EmptyResult
import Result
import RootError
import kotlinx.serialization.Serializable

enum class SensorError: RootError {
    SERVER_ERROR,
    FINGERPRINT_TIMEOUT,
    FACE_TIMEOUT,
}

@Serializable
data class KeypadResponse(val key: Char?)

@Serializable
data class StatusResponse(val status: String)

@Serializable
data class DisplayRequest(val lines: List<String>)

@Serializable
data class FaceDeleteRequest(val name: String)

@Serializable
data class FaceEnrollRequest(val name: String)

@Serializable
data class FingerPrintDeleteRequest(val id: Int)

@Serializable
data class FaceSearchResponse(val match: String?)

@Serializable
data class FingerPrintEnrollResponse(val index: Int)

@Serializable
data class FingerPrintSearchResponse(val index: Int?)


interface SensorServer {
    suspend fun displayText(lines: List<String>): EmptyResult<SensorError>
    suspend fun enrollFace(name: String): EmptyResult<SensorError>
    suspend fun recognizeFace(): Result<String?, SensorError>
    suspend fun enrollFingerPrint(): Result<Int, SensorError>
    suspend fun searchFingerPrint(): Result<Int?, SensorError>
    suspend fun deleteFingerPrint(id: Int): EmptyResult<SensorError>
    suspend fun deleteFace(id: String): EmptyResult<SensorError>

    suspend fun getStatus(): EmptyResult<SensorError>
    suspend fun getKeypadOutput(timeout: Int): Result<Char?, SensorError>
}