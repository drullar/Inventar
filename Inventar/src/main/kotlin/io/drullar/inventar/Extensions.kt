package io.drullar.inventar

import androidx.compose.runtime.mutableStateMapOf

fun <K, V> Map<K, V>.toMutableStateMap() = mutableStateMapOf(
    *this.map { entry -> entry.key to entry.value }.toTypedArray()
)
