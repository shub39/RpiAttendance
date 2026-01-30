package shub39.rpi_attendance.client.presentation.attendancelog_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.DetailedAttendanceLog
import org.jetbrains.compose.resources.vectorResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.login
import rpiattendance.client.generated.resources.logout
import shub39.rpi_attendance.client.presentation.toFormattedString

@Composable
fun AttendanceLogCard(
    log: DetailedAttendanceLog,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when (log) {
                is DetailedAttendanceLog.StudentLog -> MaterialTheme.colorScheme.primaryContainer
                is DetailedAttendanceLog.TeacherLog -> MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = when (log) {
                is DetailedAttendanceLog.StudentLog -> MaterialTheme.colorScheme.onPrimaryContainer
                is DetailedAttendanceLog.TeacherLog -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (log) {
                        is DetailedAttendanceLog.StudentLog -> "${log.student.firstName} ${log.student.lastName}"
                        is DetailedAttendanceLog.TeacherLog -> "${log.teacher.firstName} ${log.teacher.lastName}"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (log) {
                        is DetailedAttendanceLog.StudentLog -> "Student"
                        is DetailedAttendanceLog.TeacherLog -> "Teacher"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = when (log) {
                        is DetailedAttendanceLog.StudentLog -> "Roll No: ${log.student.rollNo}"
                        is DetailedAttendanceLog.TeacherLog -> "Subject: ${log.teacher.subjectTaught}"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = vectorResource(
                        when (log.log.attendanceStatus) {
                            models.AttendanceStatus.IN -> Res.drawable.login
                            models.AttendanceStatus.OUT -> Res.drawable.logout
                        }
                    ),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = log.log.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).time.toFormattedString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}