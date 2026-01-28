package shub39.rpi_attendance.client.presentation.attendancelog_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import models.Session
import models.Student
import models.Teacher
import shub39.rpi_attendance.client.presentation.theme.AppTheme
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetails(
    modifier: Modifier = Modifier,
    session: Session,
    onDismissRequest: () -> Unit
) {
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
        SessionDetails(
            session = Session(
                teacher = Teacher(
                    biometricId = "b",
                    firstName = "Teacher",
                    lastName = "last name",
                    subjectTaught = "Subject "
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
            ),
            onDismissRequest = {}
        )
    }
}