package io.drullar.inventar.ui.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ResourceLoader
import java.io.InputStream

@OptIn(ExperimentalComposeUiApi::class)
class CustomResourceLoader() : ResourceLoader {
    /**
     * [resourcePath] - path relative to [resourcesRootPath]
     */
    override fun load(resourcePath: String): InputStream =
        ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath)
            ?: throw Exception("TODO") // TODO exception
}