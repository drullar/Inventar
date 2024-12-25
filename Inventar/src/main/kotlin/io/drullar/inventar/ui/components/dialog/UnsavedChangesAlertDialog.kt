package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.roundedBorderShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    text: String,
    resolveButtonText: String,
    onResolve: () -> Unit,
    onCancel: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.border(1.dp, Color.Black, roundedBorderShape())
    ) {
        OutlinedCard(modifier = Modifier.wrapContentWidth().height(200.dp)) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(10.dp)) {
                Text(text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxHeight(0.8f))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = onResolve) {
                        Text(resolveButtonText)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun UnsavedChangesAlertDialogPreview() {
    AlertDialog(
        text = "There are unsaved changes to a product you're editing. " +
                "Save or revert the changes in order to select to continue",
        resolveButtonText = "Save changes",
        onResolve = { },
        onCancel = { }
    )
}