package com.privatetube.app.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.privatetube.app.domain.Page
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListState<T>(
    val items: List<T> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    /** Message for a failed first page; the screen shows it with a retry button. */
    val error: String? = null,
    /** A later page failed; already shown items stay usable. */
    val moreError: Boolean = false,
    val endReached: Boolean = false,
)

/**
 * Loads a list page by page. Items are de-duplicated by [keyOf] (the site repeats entries across pages),
 * and a page with nothing new ends the list. Lives in a ViewModel, so going back to the screen shows the
 * already loaded items immediately instead of reloading.
 */
open class PagedViewModel<T : Any>(
    private val keyOf: (T) -> Any,
    private val loader: suspend (page: Int) -> Page<T>,
) : ViewModel() {

    private val _state = MutableStateFlow(ListState<T>())
    val state: StateFlow<ListState<T>> = _state.asStateFlow()

    private companion object { const val MAX_EMPTY_PAGES = 6 }

    private var nextPage = 1
    private var job: Job? = null

    init { loadNext() }

    fun loadMore() {
        val s = _state.value
        if (s.loading || s.loadingMore || s.endReached || job?.isActive == true) return
        loadNext()
    }

    fun retry() {
        if (job?.isActive == true) return
        loadNext()
    }

    /**
     * Starts the list over from page 1. The old items stay on screen until the new first page arrives, then they are
     * replaced. Subclasses may override it to make the loader skip its caches.
     */
    open fun refresh() {
        job?.cancel()
        nextPage = 1
        replaceOnLoad = true
        loadNext()
    }

    private var replaceOnLoad = false

    private fun loadNext() {
        job = viewModelScope.launch {
            val first = nextPage == 1
            val replace = first && replaceOnLoad
            // While refreshing, the old items remain visible, so the screen does not flash to a spinner.
            _state.update {
                when {
                    replace -> it.copy(error = null, moreError = false, loadingMore = false)
                    first -> it.copy(loading = true, error = null)
                    else -> it.copy(loadingMore = true, moreError = false)
                }
            }
            try {
                // The site sometimes serves a page made only of videos already shown. That is not the end of the list:
                // up to a few such pages in a row are skipped before the list is considered finished.
                var skipped = 0
                var base = if (replace) emptyList() else _state.value.items
                var fresh: List<T>
                var hasMore: Boolean
                while (true) {
                    val page = loader(nextPage)
                    val known = base.mapTo(HashSet(), keyOf)
                    fresh = page.items.filter { known.add(keyOf(it)) }
                    hasMore = page.hasMore
                    nextPage++
                    if (fresh.isNotEmpty() || !hasMore || ++skipped >= MAX_EMPTY_PAGES) break
                }
                replaceOnLoad = false
                _state.update { s ->
                    val current = if (replace) emptyList() else s.items
                    s.copy(
                        items = current + fresh,
                        loading = false,
                        loadingMore = false,
                        error = null,
                        moreError = false,
                        endReached = fresh.isEmpty() || !hasMore,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update {
                    if (first) it.copy(loading = false, error = e.userMessage())
                    else it.copy(loadingMore = false, moreError = true)
                }
            }
        }
    }
}
