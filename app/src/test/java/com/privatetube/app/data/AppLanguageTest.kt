package com.privatetube.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test fun systemLanguageIsUsedWhenTheAppHasIt() {
        for (c in listOf("en", "ru", "uk", "es", "pt", "fr", "de", "it", "pl", "nl", "cs", "ja", "zh")) {
            assertEquals(c, c, AppLanguage.forSystem(c))
        }
        for (c in listOf("kk", "ky", "be")) assertEquals(c, "ru", AppLanguage.forSystem(c))
        for (c in listOf("sv", "tr", "ar", "ko", "", null)) assertEquals("en", AppLanguage.forSystem(c))
    }

    @Test fun explicitChoiceWinsOverTheSystem() {
        assertEquals("uk", AppLanguage.Ukrainian.tag("ru"))
        assertEquals("ru", AppLanguage.Russian.tag("en"))
        assertEquals("en", AppLanguage.English.tag("uk"))
        assertEquals("ja", AppLanguage.Japanese.tag("de"))
        assertEquals("ru", AppLanguage.Auto.tag("kk"))
        assertEquals(AppLanguage.Auto, AppLanguage.fromId(42))
    }

    /** The saved numbers of earlier versions must keep meaning the same language. */
    @Test fun savedIdsStayStable() {
        assertEquals(AppLanguage.Auto, AppLanguage.fromId(0))
        assertEquals(AppLanguage.Russian, AppLanguage.fromId(1))
        assertEquals(AppLanguage.Ukrainian, AppLanguage.fromId(2))
        assertEquals(AppLanguage.English, AppLanguage.fromId(3))
        assertEquals(AppLanguage.entries.size, AppLanguage.entries.map { it.id }.toSet().size)
    }
}
