import domain.SensorError

fun logError(tag: String, error: SensorError, debugMessage: String?) {
    println("[$tag Error]: $error ${debugMessage ?: ""}")
}

fun logInfo(message: String) {
    println("[INFO]: $message")
}
