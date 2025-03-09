package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.shared.Settings
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsProviderImpl : SettingsProvider {
    private var settings: MutableStateFlow<Settings> = MutableStateFlow(loadSettings())
    private fun loadSettings(): Settings =
        Settings() //TODO implement

    override fun getSettings(): StateFlow<Settings> = settings

    override fun setSettings(settings: Settings) {
        this.settings.value = settings
    }
}