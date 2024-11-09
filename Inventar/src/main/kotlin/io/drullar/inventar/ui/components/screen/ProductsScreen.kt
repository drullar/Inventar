package io.drullar.inventar.ui.components.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.drullar.inventar.ui.components.PREVIEW_COMPONENT_DEPRECATION_MESSAGE
import io.drullar.inventar.ui.components.navigation.NavigationBarPreviewContainer

@Composable
fun ProductsScreen(navigationBar: @Composable () -> Unit) {
    Column {
        navigationBar()
        Text("Products screen")
    }
}

@Preview
@Composable
@Deprecated(PREVIEW_COMPONENT_DEPRECATION_MESSAGE)
internal fun ProductsScreenPreviewContainer() {
    ProductsScreen {
        NavigationBarPreviewContainer()
    }
}

