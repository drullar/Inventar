package io.drullar.inventar

import io.drullar.inventar.shared.SortingOrder
import java.time.LocalDateTime
import java.util.StringJoiner
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

fun verifyValuesAreNotEmpty(vararg values: String) = values.none { it.isBlank() }

fun LocalDateTime.toFileAppropriateString(): String = StringJoiner("-")
    .add(dayOfMonth.toString())
    .add(month.name)
    .add(year.toString())
    .add(hour.toString())
    .add(minute.toString())
    .add(second.toString())
    .toString()