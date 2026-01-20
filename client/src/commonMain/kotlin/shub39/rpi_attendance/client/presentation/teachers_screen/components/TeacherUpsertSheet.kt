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
import rpiattendance.client.generated.resources.fingerprint
import rpiattendance.client.generated.resources.fingerprint_off
import rpiattendance.client.generated.resources.save
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeacherUpsertSheet(
    modifier: Modifier = Modifier,
    isUpdate: Boolean = false,
    enrollState: EnrollState,
    teacher: Teacher,
    onUpsert: (Teacher) -> Unit,
    onEnroll: (Teacher) -> Unit,
    onDelete: (Teacher) -> Unit,
    onDismissRequest: () -> Unit
) {
    var newTeacher by remember { mutableStateOf(teacher) }

    val isValidTeacherData = newTeacher.firstName.isNotBlank() &&
            newTeacher.lastName.isNotBlank() &&
            newTeacher.subjectTaught.isNotBlank()

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetGesturesEnabled = false,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 500.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(
                            if (isUpdate) {
                                Res.string.edit_teacher
                            } else {
                                Res.string.add_teacher
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()
                }
            }

            item {
                OutlinedTextField(
                    value = newTeacher.firstName,
                    onValueChange = { newTeacher = newTeacher.copy(firstName = it) },
                    label = { Text("First Name") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = newTeacher.lastName,
                    onValueChange = { newTeacher = newTeacher.copy(lastName = it) },
                    label = { Text("Last Name") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = newTeacher.subjectTaught,
                    onValueChange = { newTeacher = newTeacher.copy(subjectTaught = it) },
                    label = { Text("Subject Taught") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                if (isUpdate) {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        leadingContent = {
                            Icon(
                                painter = painterResource(
                                    if (newTeacher.biometricId == null) {
                                        Res.drawable.fingerprint_off
                                    } else {
                                        Res.drawable.fingerprint
                                    }
                                ),
                                contentDescription = null
                            )
                        },
                        headlineContent = {
                            Text(text = "Biometrics")
                        },
                        supportingContent = {
                            Text(
                                text = if (newTeacher.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
                                    when (enrollState) {
                                        is EnrollState.EnrollFailed -> "Enroll Failed"
                                        EnrollState.Enrolling -> "Enrolling"
                                        EnrollState.FingerprintEnrolled -> "Fingerprint Enrolled..."
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
                                    onClick = {
                                        if (newTeacher.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
                                            onEnroll(newTeacher)
                                        } else {
                                            newTeacher = newTeacher.copy(biometricId = null)
                                        }
                                    },
                                    enabled = isValidTeacherData
                                ) {
                                    Text(
                                        text = if (newTeacher.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
                                            "Enroll"
                                        } else {
                                            "Delete"
                                        }
                                    )
                                }
                            } else {
                                LoadingIndicator()
                            }
                        }
                    )
                } else {
                    ListItem(
                        headlineContent = {
                            Text("Enroll Biometrics after saving data")
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.fingerprint_off),
                                contentDescription = null
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDelete(newTeacher)
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = enrollState is EnrollState.Idle ||
                                    enrollState is EnrollState.EnrollComplete ||
                                    enrollState is EnrollState.EnrollFailed
                        ) {
                            Text(
                                text = stringResource(Res.string.delete)
                            )
                        }

                        Button(
                            onClick = {
                                onUpsert(newTeacher)
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = isValidTeacherData && teacher != newTeacher
                        ) {
                            Text(
                                text = stringResource(Res.string.save)
                            )
                        }
                    }
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
            teacher = Teacher(
                id = 0,
                biometricId = null,
                firstName = "",
                lastName = "",
                subjectTaught = ""
            ),
            onUpsert = {},
            onEnroll = {},
            onDelete = {},
            onDismissRequest = {}
        )
    }
}