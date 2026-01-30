package shub39.rpi_attendance.client.presentation.teachers_screen

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
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
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
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import models.Teacher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.add
import rpiattendance.client.generated.resources.add_teacher
import rpiattendance.client.generated.resources.delete
import rpiattendance.client.generated.resources.search
import rpiattendance.client.generated.resources.teachers
import rpiattendance.client.generated.resources.teachers_enrolled_template
import shub39.rpi_attendance.client.presentation.teachers_screen.components.TeacherInfo
import shub39.rpi_attendance.client.presentation.teachers_screen.components.TeacherUpsertSheet
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeachersScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: TeachersScreenState,
    onAction: (TeachersScreenAction) -> Unit,
) {
    val launcher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("txt", "json")),
        mode = FileKitMode.Single
    ) { file ->
        if (file != null) {
            onAction(TeachersScreenAction.ImportList(file))
        }
    }

    TeachersScreenContent(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
        onAction = onAction,
        onPickFile = { launcher.launch() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TeachersScreenContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: TeachersScreenState,
    onAction: (TeachersScreenAction) -> Unit,
    onPickFile: () -> Unit
) {
    var showTeacherAddSheet by remember { mutableStateOf(false) }
    var editTeacher by remember { mutableStateOf<Teacher?>(null) }

    Scaffold(
        modifier = Modifier.padding(contentPadding),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.teachers))
                },
                subtitle = {
                    Text(
                        text = stringResource(
                            Res.string.teachers_enrolled_template,
                            state.teachers.size
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            var menu by remember { mutableStateOf(false) }
            FloatingActionButtonMenu(
                expanded = menu,
                button = {
                    ToggleFloatingActionButton(
                        checked = menu,
                        onCheckedChange = { menu = it }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.add),
                            contentDescription = null
                        )
                    }
                }
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {
                        showTeacherAddSheet = true
                        menu = false
                    },
                    text = { Text("Add Manually") },
                    icon = {}
                )

                FloatingActionButtonMenuItem(
                    onClick = {
                        onPickFile()
                        menu = false
                    },
                    text = { Text("Import") },
                    icon = {}
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = state.teachers.isNotEmpty()
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
                    item {
                        Row(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = {
                                    onAction(TeachersScreenAction.OnChangeSearchQuery(it))
                                },
                                singleLine = true,
                                shape = MaterialTheme.shapes.extraLarge,
                                label = { Text("Search") },
                                placeholder = { Text("Name, Subject") },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.search),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            onAction(TeachersScreenAction.OnChangeSearchQuery(""))
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

                    items(state.searchResults) { teacher ->
                        TeacherInfo(
                            teacher = teacher,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEdit = { editTeacher = teacher }
                        )
                    }

                    if (state.searchResults.isNotEmpty()) {
                        item { HorizontalDivider() }
                    }

                    items(state.teachers) { teacher ->
                        TeacherInfo(
                            teacher = teacher,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onEdit = { editTeacher = teacher },
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
                            text = stringResource(Res.string.add_teacher)
                        )
                    }
                }
            }
        }
    }

    if (showTeacherAddSheet) {
        TeacherUpsertSheet(
            modifier = Modifier.imePadding(),
            isUpdate = false,
            enrollState = state.enrollState,
            teacher = Teacher(
                biometricId = null,
                firstName = "",
                lastName = "",
                subjectTaught = ""
            ),
            onUpsert = { onAction(TeachersScreenAction.UpsertTeacher(it)) },
            onEnroll = {
                onAction(TeachersScreenAction.UpsertTeacher(it))
                onAction(TeachersScreenAction.EnrollTeacher(it))
            },
            onDelete = { onAction(TeachersScreenAction.DeleteTeacher(it)) },
            onDismissRequest = {
                if (!state.enrollState.isEnrolling()) {
                    showTeacherAddSheet = false
                    onAction(TeachersScreenAction.ResetEnrollState)
                }
            }
        )
    }

    if (editTeacher != null) {
        TeacherUpsertSheet(
            modifier = Modifier.imePadding(),
            isUpdate = true,
            enrollState = state.enrollState,
            teacher = editTeacher!!,
            onUpsert = { onAction(TeachersScreenAction.UpsertTeacher(it)) },
            onEnroll = {
                onAction(TeachersScreenAction.UpsertTeacher(it))
                onAction(TeachersScreenAction.EnrollTeacher(it))
            },
            onDelete = { onAction(TeachersScreenAction.DeleteTeacher(it)) },
            onDismissRequest = {
                if (!state.enrollState.isEnrolling()) {
                    editTeacher = null
                    onAction(TeachersScreenAction.ResetEnrollState)
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        TeachersScreenContent(
            modifier = Modifier,
            contentPadding = PaddingValues(),
            state = TeachersScreenState(),
            onAction = { },
            onPickFile = {}
        )
    }
}