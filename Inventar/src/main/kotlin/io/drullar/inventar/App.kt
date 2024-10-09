package io.drullar.inventar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.drullar.inventar.components.Toolbar

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    Toolbar()
}