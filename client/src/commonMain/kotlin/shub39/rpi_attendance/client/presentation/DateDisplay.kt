package shub39.rpi_attendance.client.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.arrow_back
import rpiattendance.client.generated.resources.arrow_forward
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DateDisplay(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDate = selectedDate.toJavaLocalDate(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            content = {
                DatePicker(
                    state = datePickerState
                )
            },
            confirmButton = {
                val newDate = datePickerState.getSelectedDate()?.toKotlinLocalDate()

                TextButton(
                    onClick = {
                        newDate?.let { onDateChange(it) }
                        showDatePicker = false
                    },
                    enabled = newDate != null && newDate <= today
                ) {
                    Text(text = "Done")
                }
            }
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalIconButton(
            onClick = {
                onDateChange(
                    selectedDate.minus(
                        1, DateTimeUnit.DAY
                    )
                )
            },
            shape = ButtonGroupDefaults.connectedLeadingButtonShape
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back),
                contentDescription = null,
            )
        }

        FilledTonalButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.weight(1f),
            shape = ButtonGroupDefaults.connectedMiddleButtonPressShape
        ) {
            Text(
                text = selectedDate.toFormattedString(),
                fontWeight = FontWeight.Bold
            )
        }

        FilledTonalIconButton(
            onClick = {
                onDateChange(
                    selectedDate.plus(
                        1, DateTimeUnit.DAY
                    )
                )
            },
            shape = ButtonGroupDefaults.connectedTrailingButtonShape,
            enabled = selectedDate < today
        ) {
            Icon(
                painter = painterResource(Res.drawable.arrow_forward),
                contentDescription = null,
            )
        }
    }
}