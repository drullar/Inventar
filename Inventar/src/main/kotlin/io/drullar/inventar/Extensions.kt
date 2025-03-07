package io.drullar.inventar

import androidx.compose.runtime.mutableStateMapOf

fun <K, V> Map<K, V>.toMutableStateMap() = mutableStateMapOf(
    *this.map { entry -> entry.key to entry.value }.toTypedArray()
)

inline fun <T, R : Comparable<R>> Iterable<T>.sortedBy(
    order: SortingOrder,
    crossinline selector: (T) -> R?
): List<T> {
    return if (order == SortingOrder.ASCENDING)
        sortedWith(compareBy(selector))
    else
        sortedByDescending(selector)
}


enum class SortingOrder {
    ASCENDING,
    DESCENDING
}