package shub39.rpi_attendance.client.presentation.sessions_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import models.Session
import shub39.rpi_attendance.client.presentation.toFormattedString

@Composable
fun SessionCard(
    modifier: Modifier = Modifier,
    session: Session
) {
    var showDetails by remember { mutableStateOf(false) }

    if (showDetails) {
        SessionDetails(
            onDismissRequest = { showDetails = false },
            session = session
        )
    }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = {
            if (session.students.isNotEmpty()) showDetails = true
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.teacher.subjectTaught,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "By ${session.teacher.firstName}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${session.students.size}/${session.totalStudents} Students Present",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = session.startTime.toFormattedString(),
                    fontWeight = FontWeight.Bold
                )
                Text(text = "to")
                Text(
                    text = session.endTime.toFormattedString(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}