package io.drullar.inventar.utils.file

import java.io.File
import kotlin.io.path.createDirectories

class FileManager {
    private val appRootDirectory: File
    private val appConfigDirectory: File

    init {
        val operatingSystem = System.getProperty("os.name").lowercase()
        appRootDirectory =
            if (operatingSystem.contains("windows")) {
                windowsAppDirectory
            } else {
                throw UnsupportedOperationException("Your Operating System (${operatingSystem}) is not supported by the application")
            }

        if (!appRootDirectory.exists()) {
            appRootDirectory.toPath().createDirectories()
        }
        appConfigDirectory = File(appRootDirectory, CONFIG_DIR_NAME).also {
            if (!it.exists()) it.toPath().createDirectories()
        }
    }

    fun setupMandatoryFiles() {
        applicationFiles.values.forEach {
            if (!it.exists()) it.create()
            else it.validateFileIntegrity()
        }
    }

    fun getFile(fileType: FileType): ApplicationFile =
        applicationFiles[fileType]!!

    private val applicationFiles = mapOf<FileType, ApplicationFile>(
        FileType.Settings to SettingsFile(appConfigDirectory),
        FileType.Database to DatabaseFile(appConfigDirectory)
    )

    companion object {
        private val windowsAppDirectory: File by lazy {
            File(
                System.getenv("ProgramData"),
                "Inventar"
            )
        }
        const val CONFIG_DIR_NAME = "conf"
    }
}
