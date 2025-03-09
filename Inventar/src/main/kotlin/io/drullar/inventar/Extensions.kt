package io.drullar.inventar

import io.drullar.inventar.ui.viewmodel.delegates.getText

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
    ASCENDING(getText("order.ascending")),
    DESCENDING(getText("order.descending"));
}