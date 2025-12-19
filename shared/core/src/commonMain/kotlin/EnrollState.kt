import kotlinx.serialization.Serializable

@Serializable
sealed interface EnrollState {
    @Serializable
    data object FingerprintEnrolled: EnrollState

    @Serializable
    data object EnrollComplete: EnrollState

    @Serializable
    data class EnrollFailed(val errorMessage: String?): EnrollState
}