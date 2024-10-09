package io.drullar.inventar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Toolbar() {
    Row {
        Column {
            Button(onClick = {}) {
                Text("Button1")
            }
        }
        Column {
            Button(onClick = {}) {
                Text("Button2")
            }
        }
        Column(modifier = Modifier.navigationBarsPadding()) {
            Button(onClick = {}) {
                Text("Button2")
            }
        }
    }
}