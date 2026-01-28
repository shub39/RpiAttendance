package shub39.rpi_attendance.client.presentation.students_screen

import EnrollState.Companion.isEnrolling
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import models.Student
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.add
import rpiattendance.client.generated.resources.add_student
import rpiattendance.client.generated.resources.delete
import rpiattendance.client.generated.resources.search
import rpiattendance.client.generated.resources.students
import rpiattendance.client.generated.resources.students_enrolled_template
import shub39.rpi_attendance.client.presentation.students_screen.components.StudentInfo
import shub39.rpi_attendance.client.presentation.students_screen.components.StudentUpsertSheet
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StudentsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: StudentsScreenState,
    onAction: (StudentsScreenAction) -> Unit
) {
    var showStudentAddSheet by remember { mutableStateOf(false) }
    var editStudent by remember { mutableStateOf<Student?>(null) }

    Scaffold(
        modifier = Modifier.padding(contentPadding),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.students))
                },
                subtitle = {
                    Text(
                        text = stringResource(
                            Res.string.students_enrolled_template,
                            state.students.size
                        )
                    )
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
        AnimatedContent(
            targetState = state.students.isNotEmpty()
        ) { isNotEmpty ->
            if (isNotEmpty) {
                LazyColumn(
                    modifier = modifier,
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 60.dp,
                        start = paddingValues.calculateLeftPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stickyHeader {
                        Row(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = {
                                    onAction(
                                        StudentsScreenAction.OnChangeSearchQuery(it)
                                    )
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.extraLarge,
                                label = { Text("Search") },
                                placeholder = { Text("Name, Roll No") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.search),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            onAction(StudentsScreenAction.OnChangeSearchQuery(""))
                                        },
                                        enabled = state.searchQuery.isNotBlank()
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.delete),
                                            contentDescription = null
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }

                    items(state.searchResults) { student ->
                        StudentInfo(
                            student = student,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEdit = { editStudent = student }
                        )
                    }

                    if (state.searchResults.isNotEmpty()) {
                        item { HorizontalDivider() }
                    }

                    items(state.students) { student ->
                        StudentInfo(
                            student = student,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEdit = { editStudent = student },
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.add),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                        )
                        Text(
                            text = stringResource(Res.string.add_student)
                        )
                    }
                }
            }
        }
    }

    if (showStudentAddSheet) {
        StudentUpsertSheet(
            modifier = Modifier.imePadding(),
            isUpdate = false,
            enrollState = state.enrollState,
            student = Student(
                biometricId = null,
                firstName = "",
                lastName = "",
                rollNo = 0,
                contactEmail = "",
                contactPhone = ""
            ),
            onUpsert = { onAction(StudentsScreenAction.UpsertStudent(it)) },
            onEnroll = {
                onAction(StudentsScreenAction.UpsertStudent(it))
                onAction(StudentsScreenAction.EnrollStudent(it))
            },
            onDelete = { onAction(StudentsScreenAction.DeleteStudent(it)) },
            onDismissRequest = {
                if (!state.enrollState.isEnrolling()) {
                    showStudentAddSheet = false
                    onAction(StudentsScreenAction.ResetEnrollState)
                }
            }
        )
    }

    if (editStudent != null) {
        StudentUpsertSheet(
            modifier = Modifier.imePadding(),
            isUpdate = true,
            enrollState = state.enrollState,
            student = editStudent!!,
            onUpsert = { onAction(StudentsScreenAction.UpsertStudent(it)) },
            onEnroll = {
                onAction(StudentsScreenAction.UpsertStudent(it))
                onAction(StudentsScreenAction.EnrollStudent(it))
            },
            onDelete = { onAction(StudentsScreenAction.DeleteStudent(it)) },
            onDismissRequest = {
                if (!state.enrollState.isEnrolling()) {
                    editStudent = null
                    onAction(StudentsScreenAction.ResetEnrollState)
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        StudentsScreen(
            modifier = Modifier,
            contentPadding = PaddingValues(),
            state = StudentsScreenState(),
            onAction = { }
        )
    }
}