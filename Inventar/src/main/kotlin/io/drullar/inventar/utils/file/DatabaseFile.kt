package io.drullar.inventar.utils.file

import java.io.File


class DatabaseFile(private val parentDirectory: File) : AbstractApplicationFile() {
    override val file: File get() = File(parentDirectory, fileName)
    override val fileName = "inventar.db"
    override fun validateFileIntegrity() = Unit
}