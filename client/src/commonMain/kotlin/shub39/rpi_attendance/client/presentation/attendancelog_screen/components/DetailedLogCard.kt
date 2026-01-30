package shub39.rpi_attendance.client.presentation.attendancelog_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.DetailedAttendanceLog
import org.jetbrains.compose.resources.vectorResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.delete
import rpiattendance.client.generated.resources.login
import rpiattendance.client.generated.resources.logout
import shub39.rpi_attendance.client.presentation.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedLogCard(
    detailedLog: DetailedAttendanceLog,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDeleteDialog = false }
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.delete),
                        contentDescription = null
                    )

                    Text(
                        text = "Delete this log?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = detailedLog.log.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center
                        )
                    )

                    Button(
                        onClick = {
                            onDelete()
                            showDeleteDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when (detailedLog) {
                is DetailedAttendanceLog.StudentLog -> MaterialTheme.colorScheme.primaryContainer
                is DetailedAttendanceLog.TeacherLog -> MaterialTheme.colorScheme.secondaryContainer
            },
            contentColor = when (detailedLog) {
                is DetailedAttendanceLog.StudentLog -> MaterialTheme.colorScheme.onPrimaryContainer
                is DetailedAttendanceLog.TeacherLog -> MaterialTheme.colorScheme.onSecondaryContainer
            }
        ),
        onClick = { showDeleteDialog = true }
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
                    text = when (detailedLog) {
                        is DetailedAttendanceLog.StudentLog -> "${detailedLog.student.firstName} ${detailedLog.student.lastName}"
                        is DetailedAttendanceLog.TeacherLog -> "${detailedLog.teacher.firstName} ${detailedLog.teacher.lastName}"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = when (detailedLog) {
                        is DetailedAttendanceLog.StudentLog -> "Student"
                        is DetailedAttendanceLog.TeacherLog -> "Teacher"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = when (detailedLog) {
                        is DetailedAttendanceLog.StudentLog -> "Roll No: ${detailedLog.student.rollNo}"
                        is DetailedAttendanceLog.TeacherLog -> "Subject: ${detailedLog.teacher.subjectTaught}"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = vectorResource(
                        when (detailedLog.log.attendanceStatus) {
                            models.AttendanceStatus.IN -> Res.drawable.login
                            models.AttendanceStatus.OUT -> Res.drawable.logout
                        }
                    ),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detailedLog.log.timeStamp.toLocalDateTime(TimeZone.currentSystemDefault()).time.toFormattedString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}