package io.drullar.inventar.ui.components.window.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.utils.Icons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialog(
    text: String,
    resolveButtonText: String,
    cancelButtonText: String = getText("label.cancel"),
    headerIconPainter: Painter? = null,
    onResolve: () -> Unit,
    onCancel: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.border(1.dp, Color.Black, roundedBorderShape())
    ) {
        OutlinedCard(modifier = Modifier.wrapContentWidth().height(200.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (headerIconPainter != null) {
                    Image(
                        painter = headerIconPainter,
                        contentDescription = "AlertIcon",
                        modifier = Modifier.size(50.dp).padding(2.dp)
                    )
                } else Spacer(Modifier.height(25.dp))

                Text(
                    text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight().align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        cancelButtonText,
                        onClick = onCancel,
                        backgroundColor = Color.Red,
                        borderColor = Color.Red
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        resolveButtonText,
                        onClick = onResolve,
                        backgroundColor = Colors.DarkGreen,
                        borderColor = Colors.DarkGreen
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleActionAlertDialog(
    text: String,
    actionButtonText: String,
    onAction: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onAction,
        modifier = Modifier.border(1.dp, Color.Black, roundedBorderShape())
    ) {
        OutlinedCard(modifier = Modifier.wrapContentWidth().wrapContentHeight()) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SelectionContainer(
                    Modifier.align(Alignment.CenterHorizontally).padding(vertical = 50.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.wrapContentHeight()
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(actionButtonText, onAction)
                }
            }
        }
    }
}

@Composable
@Preview
private fun SingleActionButtonPreview() {
    SingleActionAlertDialog("There are unsaved changes to a product you're editing.\n" +
            "Save or revert the changes in order to select to continue",
        "Ok",
        {}
    )
}

@Composable
@Preview
private fun UnsavedChangesAlertDialogPreview() {
    AlertDialog(
        text = "There are unsaved changes to a product you're editing. " +
                "Save or revert the changes in order to select to continue",
        resolveButtonText = "Save changes",
        headerIconPainter = painterResource(Icons.ERROR),
        onResolve = { },
        onCancel = { }
    )
}

@Composable
@Preview
private fun OrderProductQuantityDialog() {
    AlertDialog(
        text = "The order has products with insufficient quantity.\n Would you like to complete the order anyways?",
        resolveButtonText = "Save changes",
        cancelButtonText = "Cancel",
        onResolve = { },
        onCancel = { }
    )
}