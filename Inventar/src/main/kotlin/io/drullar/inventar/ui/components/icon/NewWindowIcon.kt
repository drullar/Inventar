package io.drullar.inventar.ui.components.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.drullar.inventar.ui.utils.Icons

@Composable
fun NewWindowIcon(modifier: Modifier = Modifier) {
    Icon(painterResource(Icons.NEW_WINDOW), "Open in new window", modifier = modifier)
}