package io.drullar.inventar.utils.file

import io.drullar.inventar.shared.Settings
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.utils.JsonParser
import java.io.File
import java.util.Currency

/**
 * [fileName] from the application directory
 */
interface ApplicationFile {

    val fileName: String

    /**
     * Create file
     */
    fun create()

    /**
     * Whether the file exists on the file system or not
     */
    fun exists(): Boolean

    /**
     * Returns the absolute file path as string
     */
    fun getAbsolutePath(): String
}

/**
 * File that allow to be read from
 */
interface ReadableFile<T> {
    /**
     * Get file contents
     */
    fun read(): T
}

abstract class AbstractApplicationFile : ApplicationFile {
    protected abstract val file: File

    override fun exists(): Boolean = file.exists()

    override fun getAbsolutePath(): String = file.absolutePath
}

class SettingsFile(
    private val parentDirectory: File
) : AbstractApplicationFile(), ReadableFile<Settings> {

    override val fileName: String = "settings.json"
    override val file: File get() = File(parentDirectory, fileName)

    override fun create() {
        if (exists()) return
        file.createNewFile()
        JsonParser.write(file, defaultSettings)
    }

    override fun read(): Settings = JsonParser.read(file, Settings::class.java)

    private companion object {
        val defaultSettings = Settings(
            defaultCurrency = Currency.getInstance("BGN"),
            language = SupportedLanguage.ENGLISH
        )
    }
}

class DatabaseFile(private val parentDirectory: File) : AbstractApplicationFile() {
    override val file: File get() = File(parentDirectory, fileName)
    override val fileName = "inventar.db"

    override fun create() {
        if (!exists()) file.createNewFile()
    }
}