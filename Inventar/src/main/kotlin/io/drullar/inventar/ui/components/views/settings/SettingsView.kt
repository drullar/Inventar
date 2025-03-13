package io.drullar.inventar.ui.components.views.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import io.drullar.inventar.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    val settings = viewModel.getSettings().collectAsState()
}