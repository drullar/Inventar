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

fun isNumeric(value: String): Boolean = value.toDoubleOrNull()?.let { true } ?: false

//TODo move to some other place
enum class SortingOrder(val text: String) {
    ASCENDING("order.ascending"),
    DESCENDING("order.descending")
}

fun verifyValuesAreNotEmpty(vararg values: String) = values.none { it.isBlank() }