package com.privatetube.app.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.privatetube.app.R
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.ListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Takes focus as soon as the target is attached and the window can give it. A single `requestFocus()` in the first
 * composition often fires too early (nothing is highlighted until the first key press), so it is retried briefly.
 */
suspend fun FocusRequester.requestFocusWhenReady(attempts: Int = 25, pauseMs: Long = 60): Boolean {
    repeat(attempts) {
        if (runCatching { requestFocus() }.getOrDefault(false)) return true
        delay(pauseMs)
    }
    return false
}

/**
 * Scrolling when focus moves to an item that is not fully visible: instant (no spring animation, which feels
 * "jelly" on a remote) and with a small margin so the focused card never sits flush against the screen edge.
 */
@OptIn(ExperimentalFoundationApi::class)
private class InstantBringIntoView(private val marginFraction: Float) : BringIntoViewSpec {
    override val scrollAnimationSpec: AnimationSpec<Float> = snap()

    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
        val margin = containerSize * marginFraction
        val trailing = offset + size
        return when {
            offset >= margin && trailing <= containerSize - margin -> 0f
            size + 2 * margin >= containerSize -> offset - margin
            offset < margin -> offset - margin
            else -> trailing - (containerSize - margin)
        }
    }
}

/** [margin] is the free space (fraction of the viewport) kept around the focused item; 0 aligns it to the edge. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstantScrolling(margin: Float = 0.05f, content: @Composable () -> Unit) {
    val spec = remember(margin) { InstantBringIntoView(margin) }
    CompositionLocalProvider(LocalBringIntoViewSpec provides spec, content = content)
}

/**
 * The basic focusable building block. Focus is shown instantly (no animation) by an accent outline.
 * [onClick] fires on DPAD_CENTER / ENTER. Compose's `clickable` makes the box focusable for the remote.
 */
@Composable
fun FocusCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocused: () -> Unit = {},
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = AppColors.Card,
    focusedContainerColor: Color = AppColors.CardFocused,
    /** A disabled card cannot take focus or be clicked (e.g. while a dialog is drawn over it). */
    enabled: Boolean = true,
    /** Holding OK (or the MENU key of a remote) on the card. */
    onLongClick: (() -> Unit)? = null,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged {
                focused = it.isFocused
                if (it.isFocused) onFocused()
            }
            .clip(shape)
            .background(if (focused) focusedContainerColor else containerColor)
            .border(3.dp, if (focused) AppColors.Accent else Color.Transparent, shape)
            .then(
                if (onLongClick == null) Modifier else Modifier.onKeyEvent {
                    val menu = it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_MENU
                    if (menu && it.type == KeyEventType.KeyUp) onLongClick()
                    menu
                },
            )
            .combinedClickable(
                interactionSource = interaction, indication = null, enabled = enabled,
                onLongClick = onLongClick, onClick = onClick,
            ),
    ) { content(focused) }
}

/** All buttons look the same; the focused one is filled orange. */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    enabled: Boolean = true,
) {
    FocusCard(
        onClick = onClick,
        modifier = modifier,
        focusRequester = focusRequester,
        shape = RoundedCornerShape(10.dp),
        containerColor = AppColors.Card,
        focusedContainerColor = AppColors.Accent,
        enabled = enabled,
    ) { focused ->
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (focused) AppColors.OnAccent else AppColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
        )
    }
}

@Composable
fun VideoCard(
    video: VideoItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocused: () -> Unit = {},
    /** Smaller captions, for the narrow tiles of five-column lists. */
    compact: Boolean = false,
) {
    val menu = LocalVideoMenu.current
    FocusCard(onClick, modifier, focusRequester, onFocused, onLongClick = menu?.let { open -> { open(video) } }) {
        Column {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
                // If the image is missing or failed to load the dark box and the title below still make a usable card.
                Thumb(video.thumbUrl, Modifier.fillMaxSize())
                video.duration?.let {
                    Text(
                        text = it,
                        fontSize = if (compact) 13.sp else 18.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = video.title,
                fontSize = if (compact) 15.sp else 20.sp,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(horizontal = if (compact) 10.dp else 14.dp, vertical = if (compact) 8.dp else 10.dp),
            )
        }
    }
}

/** A thin arc that turns: it shows that something is loading without covering the picture. */
@Composable
fun ThinSpinner(modifier: Modifier = Modifier, size: Dp = 56.dp, color: Color = Color.White) {
    val turn = rememberInfiniteTransition(label = "spinner")
    val angle by turn.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1000, easing = LinearEasing)),
        label = "angle",
    )
    Canvas(modifier.size(size)) {
        val stroke = 3.dp.toPx()
        drawArc(
            color = color,
            startAngle = angle,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(this.size.width - stroke, this.size.height - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

/**
 * The one loading look used everywhere (lists, the player's first screen and the player itself), so nothing changes
 * colour or size when one hands over to the next. The spinner stays exactly in the centre whether the word is shown or not.
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, showText: Boolean = true, text: String = stringResource(R.string.loading)) {
    Box(modifier.size(width = 240.dp, height = 56.dp), contentAlignment = Alignment.Center) {
        ThinSpinner(color = AppColors.Accent)
        if (showText) {
            Text(
                text, fontSize = 24.sp, color = AppColors.TextSecondary, maxLines = 1,
                modifier = Modifier.align(Alignment.Center).offset(y = 52.dp),
            )
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier, text: String = stringResource(R.string.loading)) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingIndicator(text = text) }
}

/** Always offers a focused "Retry" button, so the screen is never a dead end for the remote. */
@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier, autoFocus: Boolean = true) {
    val requester = remember { FocusRequester() }
    // Inside the home sections the focus may be on the side menu: an error that arrives late must not pull it away.
    LaunchedEffect(Unit) { if (autoFocus) requester.requestFocusWhenReady() }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(message, fontSize = 26.sp, color = AppColors.Error, textAlign = TextAlign.Center)
            ActionButton(stringResource(R.string.retry), onRetry, focusRequester = requester)
        }
    }
}

@Composable
fun EmptyView(text: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Text(text, fontSize = 26.sp, lineHeight = 36.sp, color = AppColors.TextSecondary, textAlign = TextAlign.Center)
    }
}

/**
 * Paged grid with all the states (loading / error / empty), infinite scroll and focus memory:
 * the last focused index survives navigating to a child screen and back.
 *
 * @param requestFocusOnEnter take focus on the remembered item when the grid first appears.
 */
@Composable
fun <T : Any> PagedGrid(
    state: ListState<T>,
    columns: Int,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    emptyText: String,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    requestFocusOnEnter: Boolean = true,
    /** Picture of an item: lets the grid fetch the pictures a few rows ahead, in reading order. */
    thumbOf: ((T) -> String?)? = null,
    /** The grid shows pictures that are kept between launches (see [Thumb]). */
    permanentThumbs: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
    /** For lists items can be removed from (favorites, history): focus moves to a neighbour of the removed one. */
    refocusOnRemoval: Boolean = false,
    itemContent: @Composable (item: T, modifier: Modifier, onFocused: () -> Unit) -> Unit,
) {
    // The focused item is remembered both by position and by identity: history re-orders itself after watching.
    var lastIndex by rememberSaveable { mutableIntStateOf(0) }
    var lastKey by rememberSaveable { mutableStateOf("") }
    when {
        state.loading && state.items.isEmpty() -> LoadingView(modifier)
        state.error != null && state.items.isEmpty() -> ErrorView(state.error, onRetry, modifier, autoFocus = requestFocusOnEnter)
        state.items.isEmpty() -> EmptyView(emptyText, modifier)
        else -> {
            val gridState = rememberLazyGridState()
            var pendingFocus by remember { mutableStateOf(requestFocusOnEnter) }
            fun rememberedIndex(): Int =
                state.items.indexOfFirst { key(it).toString() == lastKey }.takeIf { it >= 0 }
                    ?: lastIndex.coerceIn(0, state.items.lastIndex)
            // Fixed when the grid appears: other items briefly getting focus must not change where it goes back to.
            var target by remember { mutableIntStateOf(rememberedIndex()) }
            LaunchedEffect(pendingFocus) {
                if (pendingFocus && gridState.layoutInfo.visibleItemsInfo.none { it.index == target }) {
                    gridState.scrollToItem(target)
                }
            }
            if (refocusOnRemoval) {
                LaunchedEffect(state.items) {
                    if (lastKey.isNotEmpty() && state.items.none { key(it).toString() == lastKey }) {
                        target = lastIndex.coerceIn(0, state.items.lastIndex)
                        lastKey = key(state.items[target]).toString()
                        pendingFocus = true
                    }
                }
            }

            // Load the next page while the user is still several rows away from the end, so the list never seems to end.
            LaunchedEffect(lastIndex, state.items.size) {
                if (lastIndex >= state.items.size - columns * 8) onLoadMore()
            }
            // A failed page is tried again by itself after a moment.
            LaunchedEffect(state.moreError) {
                if (state.moreError) { delay(3_000); onLoadMore() }
            }
            // Pictures are fetched a few rows ahead of the screen, row after row.
            if (thumbOf != null) {
                val context = LocalContext.current
                LaunchedEffect(state.items.size) {
                    snapshotFlow { gridState.firstVisibleItemIndex }.collectLatest { first ->
                        coroutineScope {
                            val end = minOf(state.items.size, first + columns * 6)
                            for (i in first until end) {
                                val url = thumbOf(state.items[i]) ?: continue
                                launch { Thumbs.load(context, url, i, permanentThumbs) }
                            }
                        }
                    }
                }
            }

            Column(modifier.fillMaxSize()) {
                InstantScrolling {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        state = gridState,
                        contentPadding = contentPadding,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        itemsIndexed(state.items, key = { _, item -> key(item) }) { index, item ->
                            val requester = remember { FocusRequester() }
                            if (pendingFocus && index == target) {
                                LaunchedEffect(Unit) {
                                    requester.requestFocusWhenReady()
                                    pendingFocus = false
                                }
                            }
                            WithThumbPriority(index) {
                                itemContent(item, Modifier.focusRequester(requester)) {
                                    if (!pendingFocus || index == target) {
                                        lastIndex = index
                                        lastKey = key(item).toString()
                                    }
                                }
                            }
                        }
                    }
                }
                // Loading the next page is silent; only a failure is mentioned (and retried automatically).
                if (state.moreError) {
                    Text(
                        text = stringResource(R.string.more_error),
                        fontSize = 18.sp,
                        color = AppColors.TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
