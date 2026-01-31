package models

import kotlinx.serialization.Serializable

/**
 * Represents a detailed attendance log, combining a raw [AttendanceLog] with
 * information about the user (either a [Teacher] or a [Student]) who created it.
 * This sealed interface allows for polymorphic serialization and handling of logs
 * from different user types.
 *
 * @property log The underlying raw attendance log entry.
 */
@Serializable
sealed interface DetailedAttendanceLog {
    val log: AttendanceLog

    @Serializable
    data class TeacherLog(
        val teacher: Teacher,
        override val log: AttendanceLog
    ) : DetailedAttendanceLog

    @Serializable
    data class StudentLog(
        val student: Student,
        override val log: AttendanceLog
    ) : DetailedAttendanceLog
}