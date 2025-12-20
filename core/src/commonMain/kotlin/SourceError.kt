sealed interface SourceError : RootError {
    enum class NetworkError : SourceError {
        REQUEST_FAILED,
        NO_INTERNET_CONNECTION,
    }
    enum class DataError : SourceError {
        SENSOR_ERROR,
        PARSE_ERROR,
        UNKNOWN_ERROR
    }
}