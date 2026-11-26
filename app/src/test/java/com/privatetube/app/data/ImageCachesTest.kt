package com.privatetube.app.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ImageCachesTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun startRemovesPreviewsAndOldCachesButKeepsCatalogPictures() {
        val root = folder.root
        listOf("http", "images", "previews", "catalog").forEach { File(root, it).apply { mkdirs() }.resolve("x").writeText("1") }
        ImageCaches.deleteLeftovers(root)
        assertFalse(File(root, "http").exists())
        assertFalse(File(root, "images").exists())
        assertFalse(File(root, "previews").exists())
        assertTrue(File(root, "catalog/x").exists())
    }
}