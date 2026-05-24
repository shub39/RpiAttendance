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
package shub39.rpi_attendance.client.presentation.teachers_screen.components

import EnrollState
import EnrollState.Companion.isEnrolling
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import models.Teacher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.add_teacher
import rpiattendance.client.generated.resources.delete
import rpiattendance.client.generated.resources.edit
import rpiattendance.client.generated.resources.edit_teacher
import rpiattendance.client.generated.resources.person_book
import rpiattendance.client.generated.resources.save
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeacherUpsertSheet(
    modifier: Modifier = Modifier,
    isUpdate: Boolean = false,
    enrollState: EnrollState,
    teacher: Teacher,
    areSensorsBusy: Boolean,
    onUpsert: (Teacher) -> Unit,
    onEnroll: (Teacher) -> Unit,
    onDelete: (Teacher) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var newTeacher by remember { mutableStateOf(teacher) }

    val isValidTeacherData =
        newTeacher.name.isNotBlank() &&
            newTeacher.id.isNotBlank() &&
            newTeacher.dept.isNotBlank() &&
            newTeacher.designation.isNotBlank()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            stickyHeader {
                Column(
                    modifier =
                        Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                    )
                    Text(
                        text =
                            stringResource(
                                if (isUpdate) {
                                    Res.string.edit_teacher
                                } else {
                                    Res.string.add_teacher
                                }
                            ),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    HorizontalDivider()
                }
            }

            item {
                OutlinedTextField(
                    value = newTeacher.name,
                    onValueChange = { newTeacher = newTeacher.copy(name = it) },
                    label = { Text("Name") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = newTeacher.id,
                    onValueChange = { newTeacher = newTeacher.copy(id = it) },
                    label = { Text("Faculty ID") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = newTeacher.dept,
                    onValueChange = { newTeacher = newTeacher.copy(dept = it) },
                    label = { Text("Department") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                )
            }

            item {
                OutlinedTextField(
                    value = newTeacher.designation,
                    onValueChange = { newTeacher = newTeacher.copy(designation = it) },
                    label = { Text("Designation") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                )
            }

            item {
                if (isUpdate) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.person_book),
                                contentDescription = null,
                            )
                        },
                        headlineContent = { Text(text = "Face Data") },
                        supportingContent = {
                            Text(
                                text =
                                    if (enrollState !is EnrollState.EnrollComplete) {
                                        when (enrollState) {
                                            is EnrollState.EnrollFailed -> "Enroll Failed"
                                            EnrollState.Enrolling -> "Enrolling"
                                            EnrollState.Idle -> "Not Enrolled"
                                        }
                                    } else {
                                        "Enrolled"
                                    }
                            )
                        },
                        trailingContent = {
                            if (!enrollState.isEnrolling()) {
                                Button(
                                    onClick = { onEnroll(newTeacher) },
                                    enabled = isValidTeacherData && !areSensorsBusy,
                                ) {
                                    Text(
                                        text =
                                            if (enrollState !is EnrollState.EnrollComplete) {
                                                "Enroll"
                                            } else {
                                                "Re-enroll"
                                            }
                                    )
                                }
                            } else {
                                LoadingIndicator()
                            }
                        },
                    )
                } else {
                    ListItem(
                        headlineContent = { Text("Enroll face after saving data") },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.person_book),
                                contentDescription = null,
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        onDelete(newTeacher)
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    enabled =
                        enrollState is EnrollState.Idle ||
                            enrollState is EnrollState.EnrollComplete ||
                            enrollState is EnrollState.EnrollFailed,
                ) {
                    Text(text = stringResource(Res.string.delete))
                }

                Button(
                    onClick = {
                        onUpsert(newTeacher)
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isValidTeacherData && teacher != newTeacher,
                ) {
                    Text(text = stringResource(Res.string.save))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        TeacherUpsertSheet(
            isUpdate = false,
            enrollState = EnrollState.Idle,
            teacher = Teacher(id = "", name = "", dept = "", designation = ""),
            onUpsert = {},
            onEnroll = {},
            onDelete = {},
            onDismissRequest = {},
            areSensorsBusy = false,
        )
    }
}
