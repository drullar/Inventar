package io.drullar.inventar.ui.viewmodel.delegates

import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import kotlinx.coroutines.flow.StateFlow

interface WindowManagerFacade {
    // Dialog window methods
    fun <T> setActiveDialog(dialogWindowType: DialogWindowType?, payload: WindowPayload<T>)
    fun getActiveDialog(): StateFlow<DialogWindowType?>
    fun <T> getActiveDialogPayload(): StateFlow<WindowPayload<T>>
    fun hasActiveDialogWindow(): Boolean

    // External window methods
    fun <T> setActiveWindow(dialogType: ExternalWindowType?, payload: WindowPayload<T>)
    fun getActiveWindow(): StateFlow<ExternalWindowType?>
    fun <T> getActiveWindowPayload(): StateFlow<WindowPayload<T>>
    fun hasActiveExternalWindow(): Boolean
}