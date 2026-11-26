package com.privatetube.app.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.ContentRepository
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.ListState
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.feature.userMessage
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.EmptyView
import com.privatetube.app.ui.PagedGrid
import com.privatetube.app.ui.VideoCard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Search with a pause after typing: requests go out only when the viewer stops entering text. */
@OptIn(FlowPreview::class)
class SearchViewModel(private val repo: ContentRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow(ListState<VideoItem>(loading = false, endReached = true))
    val state: StateFlow<ListState<VideoItem>> = _state.asStateFlow()

    /** The viewer's focus was last among the results, not in the text field: that is where a return should land. */
    var focusWasInResults = false

    /** Grows with every new list of results, so that each one starts from its first item. */
    var resultsGeneration = 0
        private set

    private var activeQuery = ""
    private var nextPage = 1
    private var job: Job? = null

    init {
        viewModelScope.launch {
            _query.debounce(DEBOUNCE_MS).map { it.trim() }.distinctUntilChanged().collectLatest { reset(it) }
        }
    }

    fun setQuery(value: String) { _query.value = value }

    /** Search right now (keyboard "Search" key) without waiting for the pause. */
    fun submit() { viewModelScope.launch { reset(_query.value.trim(), force = true) } }

    private fun reset(q: String, force: Boolean = false) {
        if (!force && q == activeQuery && (_state.value.items.isNotEmpty() || _state.value.error == null)) return
        job?.cancel()
        activeQuery = q
        resultsGeneration++
        nextPage = 1
        if (q.length < MIN_LENGTH) {
            _state.value = ListState(loading = false, endReached = true)
            return
        }
        _state.value = ListState(loading = true)
        loadNext()
    }

    fun loadMore() {
        val s = _state.value
        if (activeQuery.length < MIN_LENGTH || s.loading || s.loadingMore || s.endReached || job?.isActive == true) return
        loadNext()
    }

    fun retry() {
        if (activeQuery.length >= MIN_LENGTH && job?.isActive != true) loadNext()
    }

    private fun loadNext() {
        val q = activeQuery
        job = viewModelScope.launch {
            val first = nextPage == 1
            _state.update { if (first) it.copy(loading = true, error = null) else it.copy(loadingMore = true, moreError = false) }
            try {
                val page = repo.search(q, nextPage)
                _state.update { s ->
                    val known = s.items.mapTo(HashSet()) { it.viewKey }
                    val fresh = page.items.filter { known.add(it.viewKey) }
                    s.copy(items = s.items + fresh, loading = false, loadingMore = false, error = null, moreError = false, endReached = fresh.isEmpty())
                }
                nextPage++
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { if (first) it.copy(loading = false, error = e.userMessage()) else it.copy(loadingMore = false, moreError = true) }
            }
        }
    }

    companion object {
        const val DEBOUNCE_MS = 700L
        const val MIN_LENGTH = 2
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchSection(onOpenVideo: (String) -> Unit, restoreFocus: Boolean = false) {
    val app = rememberApp()
    val vm: SearchViewModel = viewModel(key = "search-${SiteConfig.language}", factory = viewModelFactory { initializer { SearchViewModel(app.repository) } })
    val query by vm.query.collectAsState()
    val state by vm.state.collectAsState()
    var fieldFocused by remember { mutableStateOf(false) }
    val resultsEntry = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    // Results appear while the viewer is still typing. If they took the focus then, the keyboard would close, so they
    // may take it only on a return from a video and after the "Search" key of the keyboard.
    var resultsTakeFocus by remember { mutableStateOf(restoreFocus && vm.focusWasInResults) }
    // The field keeps its own text and cursor: the cursor starts at the end (also after a return from a video), and
    // fast typing does not wait for the text to come back through the flow.
    var field by remember(vm) { mutableStateOf(TextFieldValue(vm.query.value, TextRange(vm.query.value.length))) }

    Column(Modifier.fillMaxSize().padding(top = 24.dp)) {
        BasicTextField(
            value = field,
            onValueChange = { value ->
                // The field also reports cursor moves; only new text means that the viewer is typing again.
                if (value.text != field.text) {
                    resultsTakeFocus = false
                    vm.setQuery(value.text)
                }
                field = value
            },
            singleLine = true,
            textStyle = TextStyle(color = AppColors.TextPrimary, fontSize = 28.sp),
            cursorBrush = SolidColor(AppColors.Accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                vm.submit()
                // The focus is not moved here: the old results are about to disappear and would hand it straight
                // back. The new ones take it when they arrive.
                keyboard?.hide()
                resultsTakeFocus = true
            }),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .focusProperties { down = resultsEntry }
                // Results no longer take the focus by themselves, so DOWN must always lead to them, even where the
                // text field would otherwise use the key to move the cursor.
                .onPreviewKeyEvent {
                    if (it.key != Key.DirectionDown || state.items.isEmpty()) return@onPreviewKeyEvent false
                    if (it.type == KeyEventType.KeyDown) runCatching { resultsEntry.requestFocus() }
                    true
                }
                .onFocusChanged {
                    if (it.isFocused != fieldFocused) {
                        if (it.isFocused) vm.focusWasInResults = false
                        // Also when the viewer leaves for the menu while results are loading: they must not pull back.
                        resultsTakeFocus = false
                    }
                    fieldFocused = it.isFocused
                }
                .background(AppColors.Card, RoundedCornerShape(12.dp))
                .border(3.dp, if (fieldFocused) AppColors.Accent else Color.Transparent, RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 18.dp),
            decorationBox = { inner ->
                Box {
                    if (query.isEmpty()) Text(stringResource(R.string.search_hint), fontSize = 28.sp, color = AppColors.TextSecondary)
                    inner()
                }
            },
        )
        Box(Modifier.weight(1f).focusRequester(resultsEntry).focusRestorer()) {
            when {
                query.trim().length < SearchViewModel.MIN_LENGTH && state.items.isEmpty() ->
                    EmptyView(stringResource(R.string.search_min))
                else -> key(vm.resultsGeneration) {
                    PagedGrid(
                        state = state, columns = 3, onLoadMore = vm::loadMore, onRetry = vm::retry,
                        emptyText = stringResource(R.string.search_empty, query.trim()),
                        key = { it.viewKey }, requestFocusOnEnter = resultsTakeFocus, thumbOf = { it.thumbUrl },
                    ) { item, modifier, onFocused ->
                        VideoCard(
                            item, onClick = { onOpenVideo(item.viewKey) }, modifier = modifier,
                            onFocused = { vm.focusWasInResults = true; onFocused() },
                        )
                    }
                }
            }
        }
    }
}
