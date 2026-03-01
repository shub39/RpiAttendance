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
package domain

import errors.EmptyResult
import errors.Result
import errors.RootError
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

enum class SensorError : RootError {
    SERVER_ERROR,
    FINGERPRINT_TIMEOUT,
    FACE_TIMEOUT,
}

@Serializable data class KeypadResponse(val key: Char?)

@Serializable data class StatusResponse(val status: String, val ip: String)

@Serializable data class Response(val status: String)

@Serializable data class DisplayRequest(val lines: List<String>)

@Serializable data class FaceDeleteRequest(val name: String)

@Serializable data class FaceEnrollRequest(val name: String)

@Serializable data class FingerPrintDeleteRequest(val id: Int)

@Serializable data class FaceSearchResponse(val match: String?)

@Serializable data class FingerPrintEnrollResponse(val index: Int)

@Serializable data class FingerPrintSearchResponse(val index: Int?)

sealed interface FingerprintSearchResult {
    data object NotFound : FingerprintSearchResult

    data class Found(val id: Int) : FingerprintSearchResult
}

sealed interface FaceSearchResult {
    data object NotFound : FaceSearchResult

    data class Found(val name: String) : FaceSearchResult
}

sealed interface KeypadResult {
    data object NoInput : KeypadResult

    data object Key1 : KeypadResult

    data object Key2 : KeypadResult

    data object Key3 : KeypadResult

    data object Key4 : KeypadResult

    data object Key5 : KeypadResult

    data object Key6 : KeypadResult

    data object Key7 : KeypadResult

    data object Key8 : KeypadResult

    data object Key9 : KeypadResult

    data object Key0 : KeypadResult

    data object KeyA : KeypadResult

    data object KeyB : KeypadResult

    data object KeyC : KeypadResult

    data object KeyD : KeypadResult

    data object KeyHash : KeypadResult

    data object KeyStar : KeypadResult
}

/**
 * Defines the contract for interacting with a remote sensor device. This interface provides methods
 * for controlling various hardware components like a display, face and fingerprint scanners, and a
 * keypad. All methods are suspend functions, they perform asynchronous operations.
 */
interface SensorServer {
    val isAdminOperationActive: Flow<Boolean>

    fun updateAdminOperationStatus(status: Boolean)

    val areSensorsBusy: Flow<Boolean>

    fun updateSensorsBusyState(state: Boolean)

    suspend fun displayText(lines: List<String>): EmptyResult<SensorError>

    suspend fun enrollFace(name: String): EmptyResult<SensorError>

    suspend fun recognizeFace(): Result<FaceSearchResult, SensorError>

    suspend fun enrollFingerPrint(): Result<Int, SensorError>

    suspend fun searchFingerPrint(): Result<FingerprintSearchResult, SensorError>

    suspend fun deleteFingerPrint(id: Int): EmptyResult<SensorError>

    suspend fun deleteFace(id: String): EmptyResult<SensorError>

    suspend fun getStatus(): Result<StatusResponse, SensorError>

    suspend fun getKeypadOutput(timeout: Int): Result<KeypadResult, SensorError>
}

fun Char?.toKeypadResult(): KeypadResult =
    when (this) {
        '0' -> KeypadResult.Key0
        '1' -> KeypadResult.Key1
        '2' -> KeypadResult.Key2
        '3' -> KeypadResult.Key3
        '4' -> KeypadResult.Key4
        '5' -> KeypadResult.Key5
        '6' -> KeypadResult.Key6
        '7' -> KeypadResult.Key7
        '8' -> KeypadResult.Key8
        '9' -> KeypadResult.Key9
        'A' -> KeypadResult.KeyA
        'B' -> KeypadResult.KeyB
        'C' -> KeypadResult.KeyC
        'D' -> KeypadResult.KeyD
        '*' -> KeypadResult.KeyStar
        '#' -> KeypadResult.KeyHash
        else -> KeypadResult.NoInput
    }
