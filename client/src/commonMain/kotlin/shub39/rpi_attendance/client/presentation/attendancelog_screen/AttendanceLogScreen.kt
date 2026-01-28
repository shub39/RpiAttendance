package shub39.rpi_attendance.client.presentation.attendancelog_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import models.Session
import models.Student
import models.Teacher
import org.jetbrains.compose.resources.painterResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.arrow_back
import rpiattendance.client.generated.resources.arrow_forward
import shub39.rpi_attendance.client.presentation.attendancelog_screen.components.SessionCard
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import shub39.rpi_attendance.client.presentation.toFormattedString
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AttendanceLogScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    state: AttendanceLogState,
    onAction: (AttendanceLogAction) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Attendance Logs")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledTonalIconButton(
                    onClick = {
                        onAction(
                            AttendanceLogAction.OnGetSessions(
                                state.selectedDate.minus(
                                    1, DateTimeUnit.DAY
                                )
                            )
                        )
                    },
                    shape = ButtonGroupDefaults.connectedLeadingButtonShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }

                FilledTonalButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = ButtonGroupDefaults.connectedMiddleButtonPressShape
                ) {
                    Text(
                        text = state.selectedDate.toFormattedString(),
                        fontWeight = FontWeight.Bold
                    )
                }

                FilledTonalIconButton(
                    onClick = {
                        onAction(
                            AttendanceLogAction.OnGetSessions(
                                state.selectedDate.plus(
                                    1, DateTimeUnit.DAY
                                )
                            )
                        )
                    },
                    shape = ButtonGroupDefaults.connectedTrailingButtonShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }

            if (state.sessions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.sessions) { session ->
                        SessionCard(session = session)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Sessions found",
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

    AppTheme {
        AttendanceLogScreen(
            state = AttendanceLogState(
                sessions = (0..10).map { 
                    Session(
                        teacher = Teacher(
                            biometricId = "$it",
                            firstName = "Teacher $it",
                            lastName = "last name",
                            subjectTaught = "Subject $it"
                        ),
                        startTime = time,
                        endTime = time,
                        totalStudents = 25,
                        students = (0..25).map { studentIndex ->
                            Student(
                                biometricId = "student_$studentIndex",
                                firstName = "Student $studentIndex",
                                lastName = "last name",
                                rollNo = studentIndex,
                                contactEmail = "@$studentIndex",
                                contactPhone = "ashbak$studentIndex"
                            )
                        }
                    )
                }
            ),
            onAction = {},
            padding = PaddingValues()
        )
    }
}