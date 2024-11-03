package io.drullar.inventar.ui.components.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun OrdersScreen(
    content: @Composable () -> Unit
) {
    content.invoke()
    Text("This is the orders screen")
}