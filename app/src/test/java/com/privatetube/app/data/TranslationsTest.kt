package com.privatetube.app.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/** Every language of the app has every text, with the same placeholders as the English one. */
class TranslationsTest {
    private fun strings(folder: String): Map<String, String> {
        val file = File("src/main/res/$folder/strings.xml")
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).getElementsByTagName("string")
        return (0 until nodes.length).map { nodes.item(it) as org.w3c.dom.Element }
            .filter { it.getAttribute("translatable") != "false" && it.getAttribute("name") != "app_name" }
            .associate { it.getAttribute("name") to it.textContent }
    }

    private val placeholder = Regex("""%\d\$[sd]""")

    @Test fun translationsAreComplete() {
        val english = strings("values")
        for (code in AppLanguage.entries.mapNotNull { it.code }.filter { it != "en" }) {
            val translated = strings("values-$code")
            assertEquals("$code: set of texts", english.keys, translated.keys)
            for ((name, text) in english) {
                assertEquals(
                    "$code: $name",
                    placeholder.findAll(text).map { it.value }.sorted().toList(),
                    placeholder.findAll(translated.getValue(name)).map { it.value }.sorted().toList(),
                )
            }
        }
    }
}
