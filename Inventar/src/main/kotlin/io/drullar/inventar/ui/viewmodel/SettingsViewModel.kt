package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.shared.Settings
import io.drullar.inventar.ui.viewmodel.delegate.SettingsProvider

class SettingsViewModel(settingsProvider: SettingsProvider) : SettingsProvider by settingsProvider {

    fun saveSettings(settings: Settings) {
        setSettings(settings)
    }
}