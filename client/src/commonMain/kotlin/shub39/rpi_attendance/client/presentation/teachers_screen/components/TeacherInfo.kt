/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.rpi_attendance.client.presentation.teachers_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import models.Teacher
import org.jetbrains.compose.resources.painterResource
import rpiattendance.client.generated.resources.Res
import rpiattendance.client.generated.resources.edit
import rpiattendance.client.generated.resources.fingerprint
import rpiattendance.client.generated.resources.fingerprint_off
import shub39.rpi_attendance.client.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TeacherInfo(modifier: Modifier = Modifier, teacher: Teacher, onEdit: () -> Unit) {
    Card(modifier = modifier.animateContentSize(), shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${teacher.firstName} ${teacher.lastName}",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(text = teacher.subjectTaught)
                }

                Icon(
                    painter =
                        painterResource(
                            if (teacher.biometricId == null) {
                                Res.drawable.fingerprint_off
                            } else {
                                Res.drawable.fingerprint
                            }
                        ),
                    tint =
                        if (teacher.biometricId == null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                    contentDescription = null,
                )

                IconButton(onClick = onEdit) {
                    Icon(painter = painterResource(Res.drawable.edit), contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        TeacherInfo(
            teacher =
                Teacher(
                    id = 1,
                    biometricId = "as",
                    firstName = "Shubham",
                    lastName = "Gorai",
                    subjectTaught = "Chemistry",
                ),
            onEdit = {},
        )
    }
}
