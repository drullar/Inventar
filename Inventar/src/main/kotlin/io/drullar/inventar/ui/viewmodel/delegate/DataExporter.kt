package io.drullar.inventar.ui.viewmodel.delegate

interface DataExporter<R, T> {
    fun export(request: R): T
}