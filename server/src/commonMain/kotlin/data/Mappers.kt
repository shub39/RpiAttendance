/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
        contactPhone = contactPhone,
    )
}

fun TeacherEntity.toTeacher(): Teacher {
    return Teacher(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        subjectTaught = subjectTaught,
    )
}

fun Teacher.toTeacherEntity(): TeacherEntity {
    return TeacherEntity(
        id = id,
        biometricId = biometricId,
        firstName = firstName,
        lastName = lastName,
        subjectTaught = subjectTaught,
    )
}

fun AttendanceLog.toAttendanceLogEntity(): AttendanceLogEntity {
    return AttendanceLogEntity(
        id = id,
        biometricId = biometricId,
        entityType = entityType,
        entityId = entityId,
        timeStamp = timeStamp,
        attendanceStatus = attendanceStatus,
    )
}

fun AttendanceLogEntity.toAttendanceLog(): AttendanceLog {
    return AttendanceLog(
        id = id,
        biometricId = biometricId,
        entityType = entityType,
        entityId = entityId,
        timeStamp = timeStamp,
        attendanceStatus = attendanceStatus,
    )
}
