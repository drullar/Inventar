package io.drullar.inventar.ui.viewmodel.delegates

import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.data.WindowTypeInterface
import kotlinx.coroutines.flow.StateFlow

/**
 * Manage the state of non-primary windows, such as instances of Window and DialogWindow
 * [T] - implementation of [WindowTypeInterface]
 */
interface PopupWindowManager<T : WindowTypeInterface> {
    fun setActiveWindow(type: T?)

    /**
     * [D] - payload data type
     * Set the active window type and the payload that accompanies it.
     * Not every window type requires a payload, hence the nullability of [payload].
     * [type] should be set to NULL if there shouldn't be an active window
     */
    fun <D> setActiveWindow(type: T?, payload: WindowPayload<D>)

    /**
     * Get a the active window type as state flow
     */
    fun getActiveWindow(): StateFlow<T?>

    /**
     * [D] - payload data type.
     * Set the payload that accompanies the active window
     */
    fun <D> setWindowPayload(payload: WindowPayload<D>)

    /**
     * [D] - payload data type.
     * Get the payload for the active window.
     */
    fun <D> getWindowPayload(): StateFlow<WindowPayload<D>>

    /**
     * Returns whether there is an active window
     */
    fun hasActiveWindow(): Boolean
}