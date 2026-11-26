package com.privatetube.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinTest {
    @Test fun correctPinMatchesAndWrongPinDoesNot() {
        val record = Pin.record("1234")
        assertTrue(Pin.matches("1234", record))
        assertFalse(Pin.matches("1235", record))
        assertFalse(Pin.matches("", record))
    }

    @Test fun recordDoesNotContainThePinAndIsSalted() {
        val a = Pin.record("4321")
        val b = Pin.record("4321")
        assertFalse(a.contains("4321"))
        assertNotEquals(a, b)
        assertTrue(Pin.matches("4321", a) && Pin.matches("4321", b))
    }

    @Test fun brokenOrEmptyRecordNeverMatches() {
        assertFalse(Pin.matches("1234", ""))
        assertFalse(Pin.matches("1234", "garbage"))
        assertFalse(Pin.matches("1234", "zz:zz"))
    }

    @Test fun fontSizesDefaultToMediumAndLargeIsTheOriginalSize() {
        assertEquals(FontSize.Medium, FontSize.fromId(null))
        assertEquals(FontSize.Medium, FontSize.fromId(99))
        assertEquals(listOf(FontSize.Medium, FontSize.Large), FontSize.entries.toList())
        assertTrue(FontSize.Medium.scale < FontSize.Large.scale)
        // A size saved by the version with four choices falls back to the default.
        (0..3).forEach { assertEquals(FontSize.Default, FontSize.fromId(it)) }
    }
}