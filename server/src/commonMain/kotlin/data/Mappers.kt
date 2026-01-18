package data

import data.database.AttendanceLogEntity
import data.database.StudentEntity
import data.database.TeacherEntity
import models.AttendanceLog
import models.Student
import models.Teacher

fun StudentEntity.toStudent(): Student {
    return Student(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        rollNo = rollNo,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
    )
}

fun Student.toStudentEntity(): StudentEntity {
    return StudentEntity(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        rollNo = rollNo,
        contactEmail = contactEmail,
        contactPhone = contactPhone
    )
}

fun TeacherEntity.toTeacher(): Teacher {
    return Teacher(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        subjectTaught = subjectTaught
    )
}

fun Teacher.toTeacherEntity(): TeacherEntity {
    return TeacherEntity(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        subjectTaught = subjectTaught
    )
}

fun AttendanceLog.toAttendanceLogEntity(): AttendanceLogEntity {
    return AttendanceLogEntity(
        id = id,
        biometricId = biometricId,
        entityType = entityType,
        entityId = entityId,
        timeStamp = timeStamp,
        attendanceStatus = attendanceStatus
    )
}

fun AttendanceLogEntity.toAttendanceLog(): AttendanceLog {
    return AttendanceLog(
        id = id,
        biometricId = biometricId,
        entityType = entityType,
        entityId = entityId,
        timeStamp = timeStamp,
        attendanceStatus = attendanceStatus
    )
}