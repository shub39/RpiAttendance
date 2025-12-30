package shub39.rpi_attendance.client.screens.students_screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    state: StudentsScreenState
) {
    if (state.studentsByCourse == null) return

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        stickyHeader {
            LargeTopAppBar(
                title = {
                    Text(text = state.studentsByCourse.first.name)
                }
            )
        }
    }
}