package io.drullar.inventar.ui.viewmodel.delegate

import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SharedAppStateDelegate {
    fun getPreview(): MutableStateFlow<Preview<*>?>
    fun <T> setPreview(preview: Preview<T>?)
    fun getNavigationDestination(): MutableStateFlow<NavigationDestination>
    fun getNavigationDestinationAsStateFlow(): StateFlow<NavigationDestination>
    fun setNavigationDestination(navigationDestination: NavigationDestination)
}