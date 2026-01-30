package models

import kotlinx.serialization.Serializable

@Serializable
sealed interface DetailedAttendanceLog {
    @Serializable
    data class TeacherLog(
        val teacher: Teacher,
        val log: AttendanceLog
    ) : DetailedAttendanceLog

    @Serializable
    data class StudentLog(
        val student: Student,
        val log: AttendanceLog
    ) : DetailedAttendanceLog
}