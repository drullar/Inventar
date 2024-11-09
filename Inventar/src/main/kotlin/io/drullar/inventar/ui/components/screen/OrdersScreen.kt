package io.drullar.inventar.ui.components.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun OrdersScreen(
    navigationBar: @Composable () -> Unit
) {
    navigationBar()
    Text("Orders screen")
}