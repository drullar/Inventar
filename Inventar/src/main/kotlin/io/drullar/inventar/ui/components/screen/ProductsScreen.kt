package io.drullar.inventar.ui.components.screen

import androidx.compose.runtime.Composable

@Composable
fun ProductsScreen(
    navigationBar: @Composable() () -> Unit,
    content: @Composable () -> Unit
) {
    content.invoke()
}