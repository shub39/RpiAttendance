package models

import kotlinx.datetime.LocalTime

data class Session(
    val courseId: Long,
    val teacher: Teacher,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val totalStudents: Int,
    val students: List<Student>
)