import EnrollState.Companion.isEnrolling
import kotlinx.serialization.Serializable

/**
 * Represents the various states of a fingerprint enrollment process.
 * This sealed interface is used to model the flow from starting the enrollment
 * to its successful completion or failure.
 *
 * The states are:
 * - [Idle]: The initial state before the enrollment process begins.
 * - [Enrolling]: The state when the device is actively scanning for a fingerprint.
 * - [FingerprintEnrolled]: A transient state indicating a single fingerprint scan was successful,
 *   but more scans are needed to complete the enrollment.
 * - [EnrollComplete]: The final state indicating the fingerprint has been successfully enrolled.
 * - [EnrollFailed]: A state representing that the enrollment process has failed,
 *   optionally with an error message.
 *
 * The companion object provides utility functions, such as [isEnrolling], to easily check
 * if the process is currently in an active enrollment phase.
 */
@Serializable
sealed interface EnrollState {
    @Serializable
    data object Idle: EnrollState

    @Serializable
    data object Enrolling: EnrollState

    @Serializable
    data object FingerprintEnrolled: EnrollState

    @Serializable
    data object EnrollComplete: EnrollState

    @Serializable
    data class EnrollFailed(val errorMessage: String?): EnrollState

    companion object {
        fun EnrollState.isEnrolling(): Boolean {
            return this is Enrolling || this is FingerprintEnrolled
        }
    }
}