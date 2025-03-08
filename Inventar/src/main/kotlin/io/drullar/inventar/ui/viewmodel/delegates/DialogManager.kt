package io.drullar.inventar.ui.viewmodel.delegates

import io.drullar.inventar.ui.data.DialogType
import kotlinx.coroutines.flow.StateFlow

interface DialogManager {
    fun setActiveDialog(dialogType: DialogType?)
    fun getActiveDialog(): StateFlow<DialogType?>
}