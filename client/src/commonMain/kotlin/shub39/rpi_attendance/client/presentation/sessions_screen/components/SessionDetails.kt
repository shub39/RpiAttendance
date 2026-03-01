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
package shub39.rpi_attendance.client.presentation.sessions_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Session
import models.Student
import models.Teacher
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import shub39.rpi_attendance.client.presentation.toFormattedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetails(modifier: Modifier = Modifier, session: Session, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 700.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 32.dp),
        ) {
            stickyHeader {
                Column(
                    modifier =
                        Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(bottom = 16.dp)
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = session.teacher.subjectTaught,
                        style =
                            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "${session.teacher.firstName} ${session.teacher.lastName}",
                        style =
                            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "${session.students.size}/${session.totalStudents} Students Present"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Card {
                            Text(
                                text = session.startTime.toFormattedString(),
                                modifier = Modifier.padding(8.dp),
                            )
                        }

                        Text(text = "to")

                        Card {
                            Text(
                                text = session.endTime.toFormattedString(),
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    }
                }
                HorizontalDivider()
            }

            items(session.students) { student ->
                Card(
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.padding(horizontal = 16.dp),
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = "${student.firstName} ${student.lastName}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(text = "Roll no: ${student.rollNo}")
                    }
                }
            }

            item { HorizontalDivider() }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

    AppTheme {
        SessionDetails(
            session =
                Session(
                    teacher =
                        Teacher(
                            biometricId = "b",
                            firstName = "Teacher",
                            lastName = "last name",
                            subjectTaught = "Subject ",
                        ),
                    startTime = time,
                    endTime = time,
                    totalStudents = 25,
                    students =
                        (0..20).map { studentIndex ->
                            Student(
                                biometricId = "student_$studentIndex",
                                firstName = "Student $studentIndex",
                                lastName = "last name",
                                rollNo = studentIndex,
                                contactEmail = "@$studentIndex",
                                contactPhone = "ashbak$studentIndex",
                            )
                        },
                ),
            onDismissRequest = {},
        )
    }
}
