package io.drullar.inventar

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

fun <T> result(block: () -> T) =
    try {
        success(block())
    } catch (t: Throwable) {
        failure(t)
    }

inline fun <T, R : Comparable<R>> Iterable<T>.sortedBy(
    order: SortingOrder,
    crossinline selector: (T) -> R?
): List<T> {
    return if (order == SortingOrder.ASCENDING)
        sortedWith(compareBy(selector))
    else
        sortedByDescending(selector)
}

enum class SortingOrder(val text: String) {
    ASCENDING("Ascending"),
    DESCENDING("Descending")
}