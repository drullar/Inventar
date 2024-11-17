package io.drullar.inventar.ui.components.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnsavedChangesAlertDialog(text: String, onCancel: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onCancel) {
        OutlinedCard(modifier = Modifier.width(200.dp).height(200.dp)) {
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(10.dp)) {
                Text(text)
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = onCancel) {
                        Text("Save changes")
                    }
                }
            }
        }
    }
}
