package models

import kotlinx.serialization.Serializable

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