package io.drullar.inventar.ui.components.window.external

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.DateSelector
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.utils.file.ExportRequest
import java.nio.file.Path
import java.time.LocalDate
import java.util.Locale
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

@Composable
fun DataExportWindow(
    onClose: () -> Unit,
    locale: Locale,
    onExportRequest: (ExportRequest) -> Unit
) {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(500.dp, 500.dp)
    )
    var isAlwaysOnTop by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }

    Window(
        title = "Export data",
        resizable = false,
        onCloseRequest = { onClose() },
        icon = painterResource(Icons.APP_ICON),
        state = windowState,
        alwaysOnTop = isAlwaysOnTop,
    ) {
        var fromDate by remember { mutableStateOf(LocalDate.EPOCH) }
        var untilDate by remember { mutableStateOf(LocalDate.now()) }
        var showFilePicker by remember { mutableStateOf(false) }
        var selectedDirectory by remember { mutableStateOf<Path?>(null) }

        Column {
            Text(
                "To export order history select the date wanted date range and press the export button",
                textAlign = TextAlign.Justify,
                style = appTypography().bodyMedium
            )

            DateSelector(
                locale = locale,
                preselectedDate = fromDate,
                descriptionText = getText("label.starting.date"),
                onDateSelect = { fromDate = it }
            )

            DateSelector(
                locale = locale,
                preselectedDate = untilDate,
                descriptionText = getText("label.until.date"),
                onDateSelect = { untilDate = it }
            )

            Row {
                Text(selectedDirectory?.absolutePathString() ?: getText("label.select.directory"))
                TextButton(text = getText("label.select"),
                    onClick = {
                        showFilePicker = true
                    }
                )
            }

            if (selectedDirectory != null && !isExporting)
                TextButton(
                    text = getText("label.export"),
                    onClick = {
                        onExportRequest(ExportRequest(selectedDirectory!!, fromDate, untilDate))
                        isAlwaysOnTop = false
                        windowState.isMinimized = true
                        isExporting = true
                    }
                )

            if (isExporting)
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Exporting data..."
                )
        }

        DirectoryPicker(
            show = showFilePicker,
            title = "Select export directory"
        ) { path ->
            if (path != null) {
                selectedDirectory = Path(path)
                showFilePicker = false
            }
        }
    }
}
