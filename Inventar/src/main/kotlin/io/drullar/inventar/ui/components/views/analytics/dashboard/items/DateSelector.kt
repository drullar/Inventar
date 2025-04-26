package io.drullar.inventar.ui.components.views.analytics.dashboard.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import io.drullar.inventar.ui.components.button.IconButton
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.utils.Icons
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    locale: Locale,
    modifier: Modifier = Modifier,
    preselectedDate: LocalDate = LocalDate.now(),
    descriptionText: String? = null,
    onDateSelect: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var date by remember { mutableStateOf(preselectedDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(preselectedDate, datePickerState) {
        date = preselectedDate
    }

    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (descriptionText != null) {
                Text(descriptionText, style = appTypography().bodyLarge)
            }
            Text(
                date.format(
                    DateTimeFormatter.ofPattern(
                        "dd/MMMM/YYYY",
                        locale
                    )
                ),
                style = appTypography().bodyLarge,
                modifier = Modifier.padding(start = 5.dp)
            )
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Image(
                    painterResource(Icons.CALENDAR),
                    "calendar icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        Window(onCloseRequest = { showDatePicker = false }, alwaysOnTop = true) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(getText("label.save"), {
                        datePickerState.selectedDateMillis?.let {
                            val selectedDate = LocalDate.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault()
                            )
                            onDateSelect(selectedDate)
                            showDatePicker = false
                        }
                    })
                }
            ) {
                DatePicker(datePickerState)
            }
        }
    }
}