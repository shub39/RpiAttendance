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
import EnrollState.Companion.isEnrolling
import kotlinx.serialization.Serializable

/**
 * Represents the various states of a fingerprint enrollment process. This sealed interface is used
 * to model the flow from starting the enrollment to its successful completion or failure.
 *
 * The states are:
 * - [Idle]: The initial state before the enrollment process begins.
 * - [Enrolling]: The state when the device is actively scanning for a fingerprint.
 * - [FingerprintEnrolled]: A transient state indicating a single fingerprint scan was successful,
 *   but more scans are needed to complete the enrollment.
 * - [EnrollComplete]: The final state indicating the fingerprint has been successfully enrolled.
 * - [EnrollFailed]: A state representing that the enrollment process has failed, optionally with an
 *   error message.
 *
 * The companion object provides utility functions, such as [isEnrolling], to easily check if the
 * process is currently in an active enrollment phase.
 */
@Serializable
sealed interface EnrollState {
    @Serializable data object Idle : EnrollState

    @Serializable data object Enrolling : EnrollState

    @Serializable data object FingerprintEnrolled : EnrollState

    @Serializable data object EnrollComplete : EnrollState

    @Serializable data class EnrollFailed(val errorMessage: String?) : EnrollState

    companion object {
        fun EnrollState.isEnrolling(): Boolean {
            return this is Enrolling || this is FingerprintEnrolled
        }
    }
}
