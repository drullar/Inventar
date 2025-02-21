package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(exception: Throwable, onClose: () -> Unit) {
    AlertDialog(
        text = exception.message!!,
        resolveButtonText = "Report",
        cancelButtonText = "Close",
        onCancel = onClose,
        onResolve = onClose
    )
}

@Preview
@Composable
private fun ErrorDialogPreview() {
    ErrorDialog(exception = Exception(""), onClose = {})
}