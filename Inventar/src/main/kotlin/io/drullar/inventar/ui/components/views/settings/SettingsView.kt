package io.drullar.inventar.ui.components.views.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.viewmodel.delegates.impl.SettingsProviderImpl

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    val settings = viewModel.getSettings().collectAsState()
    
}

@Preview
@Composable
private fun SettingsViewPreview() {
    SettingsView(SettingsViewModel(SettingsProviderImpl()))
}