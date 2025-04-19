package io.drullar.inventar.utils.file

import com.fasterxml.jackson.databind.DatabindException
import io.drullar.inventar.shared.OnScan
import io.drullar.inventar.shared.Settings
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.utils.parser.JsonParser
import java.io.File
import java.util.Currency

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
            language = SupportedLanguage.ENGLISH,
            onScan = OnScan.ADD_TO_ORDER
        )
    }

    override fun override(newContent: Settings) {
        JsonParser.write(file, newContent)
    }
}