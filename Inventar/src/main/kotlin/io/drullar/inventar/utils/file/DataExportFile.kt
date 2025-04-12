package io.drullar.inventar.utils.file

import io.drullar.inventar.toFileAppropriateString
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

class DataExportFile(targetDirectory: Path) : AbstractApplicationFile(),
    AppendableFile<String> {
    override val fileName: String
        get() {
            val now = LocalDateTime.now()
            return "exportData-${now.toFileAppropriateString()}$FILE_EXTENSION"
        }
    override val file: File = File(targetDirectory.toFile(), fileName)
    override fun validateFileIntegrity() {
        // Nothing to do
    }

    override fun create(): Boolean {
        file.createNewFile()
        return file.exists()
    }

    override fun append(content: String) {
        file.appendText(content)
    }

    companion object {
        private const val FILE_EXTENSION = ".csv"
    }
}

data class ExportRequest(
    val targetDirectory: Path,
    val fromDate: LocalDate,
    val untilDate: LocalDate
)