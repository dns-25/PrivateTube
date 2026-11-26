package com.privatetube.app.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.privatetube.app.domain.Author
import com.privatetube.app.domain.VideoItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class UserDataStoreTest {
    @get:Rule val tmp = TemporaryFolder()

    private fun TestScope.store(): UserDataStore =
        UserDataStore(PreferenceDataStoreFactory.create(scope = backgroundScope) { tmp.newFile("prefs.preferences_pb").also { it.delete() } })

    private fun video(n: Int) = VideoItem("key$n", "Title $n", "https://example.test/$n.jpg", "1:0$n")

    private fun author(n: Int) = Author("Author $n", "/model/a$n/videos", "https://example.test/a$n.jpg")

    @Test fun subscriptionsAreNewestFirstWithoutDuplicates() = runTest {
        val s = store()
        assertTrue(s.subscriptions.first().isEmpty())
        s.subscribe(author(1)); s.subscribe(author(2)); s.subscribe(author(1).copy(name = "Renamed"))
        assertEquals(listOf(author(2), author(1)), s.subscriptions.first())
        s.unsubscribe(author(2).path)
        assertEquals(listOf(author(1)), s.subscriptions.first())
    }

    @Test fun importAddsOnlyWhatIsMissingAndRemovesNothing() = runTest {
        val s = store()
        s.toggleFavorite(video(1)); s.subscribe(author(1))
        val result = s.merge(
            BackupData(
                favorites = listOf(video(1).copy(title = "Old title"), video(2), video(2)),
                subscriptions = listOf(author(1), author(3)),
            ),
        )
        assertEquals(MergeResult(favorites = 1, subscriptions = 1, history = 0), result)
        assertEquals(listOf(video(1), video(2)), s.favorites.first())
        assertEquals(listOf(author(1), author(3)), s.subscriptions.first())
        assertEquals(MergeResult(0, 0, 0), s.merge(s.backupData()))
    }

    @Test fun importRestoresHistoryAndPositionsWithoutOverridingLocalOnes() = runTest {
        val s = store()
        s.addToHistory(video(1)); s.savePosition("key1", 50_000, 600_000)
        val result = s.merge(
            BackupData(history = listOf(video(2), video(1)), positions = mapOf("key1" to 99_000L, "key2" to 70_000L)),
        )
        assertEquals(MergeResult(favorites = 0, subscriptions = 0, history = 1), result)
        assertEquals(listOf("key1", "key2"), s.history.first().map { it.viewKey })
        assertEquals(50_000L, s.position("key1"))
        assertEquals(70_000L, s.position("key2"))
    }

    @Test fun importedHistoryRespectsTheLimit() = runTest {
        val s = store()
        s.addToHistory(video(1000))
        val many = (1..UserDataStore.HISTORY_LIMIT + 20).map { video(it) }
        assertEquals(UserDataStore.HISTORY_LIMIT - 1, s.merge(BackupData(history = many)).history)
        assertEquals(UserDataStore.HISTORY_LIMIT, s.history.first().size)
        assertEquals("key1000", s.history.first().first().viewKey)
    }

    @Test fun ageConfirmationDefaultsToFalseAndPersists() = runTest {
        val s = store()
        assertFalse(s.ageConfirmed.first())
        s.setAgeConfirmed(true)
        assertTrue(s.ageConfirmed.first())
        s.setAgeConfirmed(false)
        assertFalse(s.ageConfirmed.first())
    }

    @Test fun favoritesToggleAddsNewestFirstAndRemoves() = runTest {
        val s = store()
        s.toggleFavorite(video(1)); s.toggleFavorite(video(2))
        assertEquals(listOf("key2", "key1"), s.favorites.first().map { it.viewKey })
        assertTrue(s.isFavorite("key1"))
        s.toggleFavorite(video(1))
        assertEquals(listOf("key2"), s.favorites.first().map { it.viewKey })
        assertFalse(s.isFavorite("key1"))
    }

    @Test fun historyMovesRepeatedVideoToTopAndIsBounded() = runTest {
        val s = store()
        s.addToHistory(video(1)); s.addToHistory(video(2)); s.addToHistory(video(1))
        assertEquals(listOf("key1", "key2"), s.history.first().map { it.viewKey })
        repeat(UserDataStore.HISTORY_LIMIT + 20) { s.addToHistory(VideoItem("k$it", "t$it")) }
        assertEquals(UserDataStore.HISTORY_LIMIT, s.history.first().size)
    }

    @Test fun positionsAreSavedButNotForTheStartOrTheEnd() = runTest {
        val s = store()
        val dur = 600_000L
        s.savePosition("a", 120_000, dur)
        assertEquals(120_000L, s.position("a"))
        s.savePosition("a", 3_000, dur)             // too early to be worth resuming
        assertEquals(0L, s.position("a"))
        s.savePosition("b", 300_000, dur)
        s.savePosition("b", dur - 5_000, dur)        // watched to the end
        assertEquals(0L, s.position("b"))
    }

    @Test fun clearHistoryAlsoForgetsPositions() = runTest {
        val s = store()
        s.addToHistory(video(1)); s.savePosition("key1", 100_000, 600_000)
        s.clearHistory()
        assertTrue(s.history.first().isEmpty())
        assertEquals(0L, s.position("key1"))
    }

    @Test fun refreshStoredUpdatesThumbnailInFavorites() = runTest {
        val s = store()
        s.toggleFavorite(video(1))
        s.refreshStored(video(1).copy(thumbUrl = "https://example.test/fresh.jpg"))
        assertEquals("https://example.test/fresh.jpg", s.favorites.first().single().thumbUrl)
    }

    @Test fun preferredQualityDefaultsTo720() = runTest {
        val s = store()
        assertEquals(720, s.preferredQuality.first())
        s.setPreferredQuality(0)
        assertEquals(0, s.preferredQuality.first())
    }
}
