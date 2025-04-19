package io.drullar.inventar.utils.file

import com.fasterxml.jackson.databind.DatabindException
import io.drullar.inventar.shared.OnScan
import io.drullar.inventar.shared.Settings
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.utils.parser.JsonParser
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

interface AppendableFile<T> {
    /**
     * Append contents
     */
    fun append(content: T)
}

abstract class AbstractApplicationFile : ApplicationFile {
    protected abstract val file: File

    override fun exists(): Boolean = file.exists()

    override fun getAbsolutePath(): String = file.absolutePath

    override fun create(): Boolean {
        if (!file.exists()) {
            file.createNewFile()
            return true
        }
        return false
    }
}