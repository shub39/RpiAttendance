package shub39.rpi_attendance.client.presentation.sessions_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Session
import models.Student
import models.Teacher
import shub39.rpi_attendance.client.presentation.DateDisplay
import shub39.rpi_attendance.client.presentation.sessions_screen.components.SessionCard
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SessionsScreen(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    state: SessionsState,
    onAction: (SessionsAction) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    LaunchedEffect(Unit) {
        onAction(SessionsAction.OnGetSessions(today))
    }

    Scaffold(
        modifier = modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Sessions")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DateDisplay(
                modifier = Modifier.padding(horizontal = 16.dp),
                selectedDate = state.selectedDate,
                onDateChange = { onAction(SessionsAction.OnGetSessions(it)) }
            )

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
        SessionsScreen(
            state = SessionsState(
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