package io.drullar.inventar.utils.bootstrap

import io.drullar.inventar.utils.file.DatabaseFile
import io.drullar.inventar.utils.file.FileManager
import io.drullar.inventar.utils.file.FileType

class ApplicationBootstrapper(
    private val fileManager: FileManager,
    private val databaseBootstrapperFactory: (DatabaseFile) -> DatabaseBootstrapper
) : Bootstrapper {

    override fun bootstrap() {
        fileManager.setupMandatoryFiles()
        val databaseFile = fileManager.getFile<DatabaseFile>(FileType.Database)
        databaseBootstrapperFactory(databaseFile).bootstrap()
    }
}