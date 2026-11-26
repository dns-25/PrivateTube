package com.privatetube.app.feature

import com.privatetube.app.core.network.SiteException
import com.privatetube.app.domain.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PagedViewModelTest {
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun vm(loader: suspend (Int) -> Page<String>) = PagedViewModel<String>({ it }, loader)

    @Test fun firstPageLoadsOnCreation() {
        val vm = vm { Page(listOf("a", "b"), it, true) }
        val s = vm.state.value
        assertEquals(listOf("a", "b"), s.items)
        assertFalse(s.loading)
        assertNull(s.error)
    }

    @Test fun loadMoreAppendsAndDropsDuplicatesAcrossPages() {
        val pages = mapOf(1 to listOf("a", "b"), 2 to listOf("b", "c"))
        val vm = vm { Page(pages[it].orEmpty(), it, true) }
        vm.loadMore()
        assertEquals(listOf("a", "b", "c"), vm.state.value.items)
    }

    @Test fun pageWithNothingNewEndsTheList_andLoadMoreStopsCallingTheLoader() {
        var calls = 0
        val vm = vm { calls++; Page(if (it == 1) listOf("a") else listOf("a"), it, true) }
        vm.loadMore() // page 2 brings only a duplicate
        assertTrue(vm.state.value.endReached)
        val before = calls
        vm.loadMore(); vm.loadMore()
        assertEquals(before, calls)
    }

    @Test fun aRepeatedPageIsSkippedAndTheListContinues() {
        val pages = mapOf(1 to listOf("a", "b"), 2 to listOf("a", "b"), 3 to listOf("c"))
        val vm = vm { Page(pages[it].orEmpty(), it, true) }
        vm.loadMore()
        assertEquals(listOf("a", "b", "c"), vm.state.value.items)
        assertFalse(vm.state.value.endReached)
    }

    @Test fun firstPageFailureShowsMessage_andRetryRecovers() {
        var fail = true
        val vm = vm {
            if (fail) throw SiteException.Http(429)
            Page(listOf("x"), it, true)
        }
        assertNotNull(vm.state.value.error)
        assertTrue(vm.state.value.error!!.contains("429"))
        assertTrue(vm.state.value.items.isEmpty())
        fail = false
        vm.retry()
        assertEquals(listOf("x"), vm.state.value.items)
        assertNull(vm.state.value.error)
    }

    @Test fun laterPageFailureKeepsItemsAndAllowsRetry() {
        var failPage2 = true
        val vm = vm {
            if (it == 2 && failPage2) throw SiteException.Timeout()
            Page(listOf("p$it"), it, true)
        }
        vm.loadMore()
        assertEquals(listOf("p1"), vm.state.value.items)
        assertTrue(vm.state.value.moreError)
        assertNull(vm.state.value.error)
        failPage2 = false
        vm.loadMore()
        assertEquals(listOf("p1", "p2"), vm.state.value.items)
        assertFalse(vm.state.value.moreError)
    }
}
