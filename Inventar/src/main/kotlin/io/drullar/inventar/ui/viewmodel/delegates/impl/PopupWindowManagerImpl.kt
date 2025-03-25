package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.ui.viewmodel.delegates.PopupWindowManager
import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.data.WindowTypeInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PopupWindowManagerImpl<T : WindowTypeInterface> : PopupWindowManager<T> {

    private val activeWindow = MutableStateFlow<T?>(null)
    private var payload: WindowPayload<*>? = null

    override fun setActiveWindow(type: T?) {
        activeWindow.value = type
    }

    override fun <D> setActiveWindow(type: T?, payload: WindowPayload<D>?) {
        setActiveWindow(type)
        if (type == null) setWindowPayload<Unit>(null)
        else setWindowPayload(payload)
    }

    override fun getActiveWindow(): StateFlow<T?> {
        return activeWindow
    }

    override fun <T> setWindowPayload(payload: WindowPayload<T>?) {
        this.payload = payload
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getWindowPayload(): WindowPayload<T> {
        return payload as WindowPayload<T>
    }

    override fun hasActiveWindow(): Boolean {
        return activeWindow.value != null
    }
}