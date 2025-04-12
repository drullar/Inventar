package io.drullar.inventar.ui.viewmodel.delegate

interface DataExporter<T> {
    fun export(request: T)
}