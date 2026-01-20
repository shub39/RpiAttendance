package shub39.rpi_attendance.client.presentation.students_screen.components

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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import models.Student
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.add_student
import rpiattendance.client.generated.resources.delete
import rpiattendance.client.generated.resources.edit
import rpiattendance.client.generated.resources.edit_student
import rpiattendance.client.generated.resources.fingerprint
import rpiattendance.client.generated.resources.fingerprint_off
import rpiattendance.client.generated.resources.save
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StudentUpsertSheet(
    modifier: Modifier = Modifier,
    isUpdate: Boolean = false,
    enrollState: EnrollState,
    student: Student,
    onUpsert: (Student) -> Unit,
    onEnroll: (Student) -> Unit,
    onDelete: (Student) -> Unit,
    onDismissRequest: () -> Unit
) {
    var newStudent by remember { mutableStateOf(student) }

    val isValidStudentData = newStudent.firstName.isNotBlank() &&
            newStudent.lastName.isNotBlank() &&
            newStudent.rollNo > 0 &&
            newStudent.contactEmail.isNotBlank() &&
            newStudent.contactPhone.isNotBlank()

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
                                Res.string.edit_student
                            } else {
                                Res.string.add_student
                            }
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()
                }
            }

            item {
                OutlinedTextField(
                    value = newStudent.firstName,
                    onValueChange = { newStudent = newStudent.copy(firstName = it) },
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
                    value = newStudent.lastName,
                    onValueChange = { newStudent = newStudent.copy(lastName = it) },
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
                    value = newStudent.rollNo.toString(),
                    onValueChange = {
                        if (it.toIntOrNull() != null) {
                            newStudent = newStudent.copy(rollNo = it.toInt())
                        }
                    },
                    label = { Text("Roll No") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = newStudent.contactEmail,
                    onValueChange = { newStudent = newStudent.copy(contactEmail = it) },
                    label = { Text("Email") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = newStudent.contactPhone,
                    onValueChange = { newStudent = newStudent.copy(contactPhone = it) },
                    label = { Text("Phone") },
                    shape = MaterialTheme.shapes.large,
                    singleLine = true,
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
                                    if (newStudent.biometricId == null) {
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
                                text = if (newStudent.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
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
                                        if (newStudent.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
                                            onEnroll(newStudent)
                                        } else {
                                            newStudent = newStudent.copy(biometricId = null)
                                        }
                                    },
                                    enabled = isValidStudentData
                                ) {
                                    Text(
                                        text = if (newStudent.biometricId == null && enrollState !is EnrollState.EnrollComplete) {
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
                                onDelete(newStudent)
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
                                onUpsert(newStudent)
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = isValidStudentData && student != newStudent
                        ) {
                            Text(text = stringResource(Res.string.save))
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
        StudentUpsertSheet(
            student = Student(
                id = 1,
                biometricId = "1",
                firstName = "Shubham",
                lastName = "Gorai",
                rollNo = 120,
                contactEmail = "gmail",
                contactPhone = "123"
            ),
            onUpsert = { },
            onDismissRequest = { },
            enrollState = EnrollState.Idle,
            onEnroll = {},
            onDelete = {}
        )
    }
}