package com.privatetube.app.data

import com.privatetube.app.domain.Author
import com.privatetube.app.domain.VideoItem
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Calendar
import java.util.GregorianCalendar

class BackupTest {
    @get:Rule val tmp = TemporaryFolder()

    private val data = BackupData(
        favorites = listOf(VideoItem("abc123", "Секретное название", "https://example.test/t.jpg", "10:00")),
        subscriptions = listOf(Author("Some Author", "/model/some-author/videos")),
        history = listOf(VideoItem("def456", "Watched")),
        positions = mapOf("def456" to 120_000L),
    )

    @Test fun whatWasExportedComesBack() {
        assertEquals(data, BackupCodec.decode(BackupCodec.encode(data)))
        assertEquals(BackupData(), BackupCodec.decode(BackupCodec.encode(BackupData())))
    }

    @Test fun nothingReadableIsInTheFile() {
        val text = String(BackupCodec.encode(data), Charsets.ISO_8859_1)
        for (word in listOf("abc123", "def456", "history", "example.test", "Some Author", "model", "favorites", "http")) {
            assertFalse(word, text.contains(word))
        }
        // and two exports of the same lists do not look the same
        assertFalse(BackupCodec.encode(data).contentEquals(BackupCodec.encode(data)))
    }

    @Test fun foreignOrDamagedFilesAreRejected() {
        val damaged = BackupCodec.encode(data).also { it[it.size - 1] = (it[it.size - 1] + 1).toByte() }
        for (bytes in listOf(ByteArray(0), ByteArray(5), "{\"favorites\":[]}".toByteArray(), damaged)) {
            assertThrows(BackupFormatException::class.java) { BackupCodec.decode(bytes) }
        }
    }

    @Test fun theFileNameLooksLikeASettingsFile() {
        val date = GregorianCalendar(2026, Calendar.NOVEMBER, 26, 2, 49).time
        assertEquals("user_preferences_26_11_2026_02_49.dat", BackupFiles.name(date))
    }

    @Test fun aSecondExportInTheSameMinuteDoesNotOverwriteTheFirst() {
        val dir = File(tmp.root, "Download")
        val name = "user_preferences_26_11_2026_02_49.dat"
        val one = BackupFiles.write(dir, name, byteArrayOf(1))
        val two = BackupFiles.write(dir, name, byteArrayOf(2))
        assertEquals(name, one.name)
        assertEquals("user_preferences_26_11_2026_02_49_2.dat", two.name)
        assertArrayEquals(byteArrayOf(1), one.readBytes())
    }

    @Test fun onlyBackupsAreListedNewestFirst() {
        val dir = tmp.newFolder("d")
        val old = File(dir, "pt_configuration_01_01_2026_10_00.cfg").apply { writeText("a"); setLastModified(1_000_000) }
        val new = File(dir, "user_preferences_26_11_2026_02_49.dat").apply { writeText("b"); setLastModified(2_000_000) }
        File(dir, "notes.txt").writeText("c")
        File(dir, "other.cfg").writeText("d")
        File(dir, "other.dat").writeText("e")
        // The two parts of a name are not mixed: only what the app itself has ever written is listed.
        File(dir, "user_preferences_x.cfg").writeText("f")
        File(dir, "pt_configuration_x.dat").writeText("g")
        assertEquals(listOf(new, old), BackupFiles.find(listOf(dir, dir, File(dir, "missing"))))
        assertTrue(BackupFiles.isBackupName("PT_CONFIGURATION_x.CFG"))
        assertTrue(BackupFiles.isBackupName("USER_PREFERENCES_x.DAT"))
    }
}