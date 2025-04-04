package io.drullar.inventar.utils.file

import com.fasterxml.jackson.databind.DatabindException
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
     * Create file. Returns whether the file was created during this method invocation
     */
    fun create(): Boolean

    /**
     * Whether the file exists on the file system or not
     */
    fun exists(): Boolean

    /**
     * Returns the absolute file path as string
     */
    fun getAbsolutePath(): String

    /**
     * Validate file contents integrity
     */
    fun validateFileIntegrity()
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

interface ModifiableFile<T> {
    /**
     * Override contents
     */
    fun override(newContent: T)
}

abstract class AbstractApplicationFile : ApplicationFile {
    protected abstract val file: File

    override fun exists(): Boolean = file.exists()

    override fun getAbsolutePath(): String = file.absolutePath

    override fun validateFileIntegrity() = Unit

    override fun create(): Boolean {
        if (!file.exists()) {
            file.createNewFile()
            return true
        }
        return false
    }
}

class SettingsFile(
    private val parentDirectory: File
) : AbstractApplicationFile(), ReadableFile<Settings>, ModifiableFile<Settings> {

    override val fileName: String = "settings.json"
    override val file: File get() = File(parentDirectory, fileName)

    override fun create(): Boolean {
        val fileCreatedNow = super.create()
        if (fileCreatedNow) JsonParser.write(file, defaultSettings)
        return fileCreatedNow
    }

    override fun read(): Settings =
        JsonParser.read(file, Settings::class.java)

    override fun validateFileIntegrity() {
        try {
            read()
        } catch (e: DatabindException) {
            //TODO log error and performed action
            file.delete()
            create()
        }
    }

    private companion object {
        val defaultSettings = Settings(
            defaultCurrency = Currency.getInstance("BGN"),
            language = SupportedLanguage.ENGLISH
        )
    }

    override fun override(newContent: Settings) {
        JsonParser.write(file, newContent)
    }
}

class DatabaseFile(private val parentDirectory: File) : AbstractApplicationFile() {
    override val file: File get() = File(parentDirectory, fileName)
    override val fileName = "inventar.db"
}