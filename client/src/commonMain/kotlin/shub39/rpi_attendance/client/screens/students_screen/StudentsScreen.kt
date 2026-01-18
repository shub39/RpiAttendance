package shub39.rpi_attendance.client.screens.students_screen

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
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
    var showEnrollSheet by remember { mutableStateOf(false) }

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
                onClick = { showEnrollSheet = true }
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
                    onClick = {}
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }

    if (showEnrollSheet) {

    }
}