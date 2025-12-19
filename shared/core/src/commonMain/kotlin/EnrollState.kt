sealed interface EnrollState {
    data object FingerprintEnrolled: EnrollState
    data object FaceEnrolled: EnrollState
    data object EnrollComplete: EnrollState
    data class EnrollFailed(val errorMessage: String?): EnrollState
}