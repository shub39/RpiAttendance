package data

import data.database.AttendanceLogEntity
import data.database.CourseEntity
import data.database.StudentEntity
import data.database.TeacherEntity
import models.AttendanceLog
import models.Course
import models.Student
import models.Teacher

fun StudentEntity.toStudent(): Student {
    return Student(
        id = id,
        courseId = courseId,
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
        courseId = courseId,
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

fun CourseEntity.toCourse(): Course {
    return Course(
        id = id,
        name = name,
        description = description,
        code = code
    )
}

fun Course.toCourseEntity(): CourseEntity {
    return CourseEntity(
        id = id,
        name = name,
        description = description,
        code = code
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