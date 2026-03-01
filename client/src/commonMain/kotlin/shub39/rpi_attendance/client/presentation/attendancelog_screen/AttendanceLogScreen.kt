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
package shub39.rpi_attendance.client.presentation.attendancelog_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.AttendanceLog
import models.AttendanceStatus
import models.DetailedAttendanceLog
import models.EntityType
import models.Student
import models.Teacher
import shub39.rpi_attendance.client.presentation.DateDisplay
import shub39.rpi_attendance.client.presentation.attendancelog_screen.components.DetailedLogCard
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceLogScreen(
    state: AttendanceLogState,
    padding: PaddingValues,
    onAction: (AttendanceLogAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    LaunchedEffect(Unit) { onAction(AttendanceLogAction.OnLoadDate(today)) }

    Scaffold(
        modifier = modifier.padding(padding),
        topBar = { TopAppBar(title = { Text(text = "Attendance Log") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            DateDisplay(
                modifier = Modifier.padding(horizontal = 16.dp),
                selectedDate = state.selectedDate,
                onDateChange = { onAction(AttendanceLogAction.OnLoadDate(it)) },
            )

            if (state.filteredDetailedLogs.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 60.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(state.filteredDetailedLogs) { detailedLogs ->
                        DetailedLogCard(
                            detailedLog = detailedLogs,
                            onDelete = {
                                onAction(AttendanceLogAction.OnDeleteLog(detailedLogs.log))
                            },
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No Logs found")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AttendanceLogScreen(
            state =
                AttendanceLogState(
                    filteredDetailedLogs =
                        (0..100).map { num ->
                            if (num % 2 == 0) {
                                DetailedAttendanceLog.StudentLog(
                                    student =
                                        Student(
                                            id = 0,
                                            biometricId = "asdf",
                                            firstName = "John",
                                            lastName = "Doe",
                                            rollNo = 1234,
                                            contactEmail = "johndoe@example.com",
                                            contactPhone = "1234567890",
                                        ),
                                    log =
                                        AttendanceLog(
                                            id = 0,
                                            biometricId = "asdf",
                                            entityType = EntityType.STUDENT,
                                            entityId = 0,
                                            timeStamp = Clock.System.now(),
                                            attendanceStatus = AttendanceStatus.entries.random(),
                                        ),
                                )
                            } else {
                                DetailedAttendanceLog.TeacherLog(
                                    teacher =
                                        Teacher(
                                            id = 0,
                                            biometricId = "asdf",
                                            firstName = "Jane",
                                            lastName = "Doe",
                                            subjectTaught = "MAD",
                                        ),
                                    log =
                                        AttendanceLog(
                                            id = 0,
                                            biometricId = "asdf",
                                            entityType = EntityType.STUDENT,
                                            entityId = 0,
                                            timeStamp = Clock.System.now() - 1.hours,
                                            attendanceStatus = AttendanceStatus.entries.random(),
                                        ),
                                )
                            }
                        }
                ),
            onAction = {},
            padding = PaddingValues(),
        )
    }
}
