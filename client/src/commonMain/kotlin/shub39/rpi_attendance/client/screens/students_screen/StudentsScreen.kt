package shub39.rpi_attendance.client.screens.students_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import models.Student
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.add
import rpiattendance.client.generated.resources.students
import rpiattendance.client.generated.resources.students_enrolled_template

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StudentsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: StudentsScreenState,
    onAction: (StudentsScreenAction) -> Unit
) {
    var showStudentAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(contentPadding),
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(text = stringResource(Res.string.students))
                },
                subtitle = {
                    Text(text = stringResource(Res.string.students_enrolled_template, state.students.size))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showStudentAddSheet = true }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add),
                    contentDescription = "Add Student"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 60.dp,
                start = paddingValues.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 16.dp
            )
        ) {
            items(state.students) { student ->
                Text(student.firstName)
                Text(student.lastName)

                Button(
                    onClick = {
                        onAction(StudentsScreenAction.DeleteStudent(student))
                    }
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }

    if (showStudentAddSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showStudentAddSheet = false
            }
        ) {
            Column {
                var firstName by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it }
                )
                var lastName by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it }
                )
                var rollNo by remember { mutableIntStateOf(0) }
                OutlinedTextField(
                    value = rollNo.toString(),
                    onValueChange = {
                        if (it.toIntOrNull() != null) {
                            rollNo = it.toInt()
                        }
                    }
                )
                var email by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it }
                )
                var phone by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it }
                )

                Text(state.enrollState.toString())

                Button(
                    onClick = {
                        showStudentAddSheet = false
                    }
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        onAction(StudentsScreenAction.AddStudent(
                            Student(
                                id = 0,
                                biometricId = null,
                                firstName = firstName,
                                lastName = lastName,
                                rollNo = rollNo,
                                contactEmail = email,
                                contactPhone = phone
                            )
                        ))
                        showStudentAddSheet = false
                    },
                    enabled = firstName.isNotBlank() && lastName.isNotBlank() && rollNo > 0 && email.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}