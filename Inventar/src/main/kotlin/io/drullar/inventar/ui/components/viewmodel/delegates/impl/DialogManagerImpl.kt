package io.drullar.inventar.ui.components.viewmodel.delegates.impl

import io.drullar.inventar.ui.components.viewmodel.delegates.DialogManager
import io.drullar.inventar.ui.data.DialogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DialogManagerImpl : DialogManager {

    private val activeDialog = MutableStateFlow<DialogType?>(null)
    override fun setActiveDialog(dialog: DialogType?) {
        activeDialog.value = dialog
    }

    override fun getActiveDialog(): StateFlow<DialogType?> = activeDialog
}