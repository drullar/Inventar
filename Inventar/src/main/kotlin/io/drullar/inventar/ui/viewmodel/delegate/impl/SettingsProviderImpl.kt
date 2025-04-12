package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.shared.Settings
import io.drullar.inventar.ui.viewmodel.delegate.SettingsProvider
import io.drullar.inventar.utils.file.FileManager
import io.drullar.inventar.utils.file.FileType
import io.drullar.inventar.utils.file.SettingsFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsProviderImpl(
    private val fileManager: FileManager
) : SettingsProvider {
    private var settings: MutableStateFlow<Settings> = MutableStateFlow(
        fileManager.getFile<SettingsFile>(FileType.Settings).read()
    )

    override fun getSettings(): StateFlow<Settings> {
        return settings
    }

    override fun setSettings(settings: Settings) {
        this.settings.value = settings
        fileManager.getFile<SettingsFile>(FileType.Settings).override(settings)
    }
}