package io.drullar.inventar.ui.components.window.dialog

import androidx.compose.runtime.Composable
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.utils.file.DataExportFile

@Composable
fun ExportResultDialog(exportFile: DataExportFile, onAcknowledge: () -> Unit) {
    SingleActionAlertDialog(
        text = getText("label.export.completed", exportFile.getAbsolutePath()),
        actionButtonText = getText("label.acknowledge"),
        onAction = onAcknowledge
    )
}