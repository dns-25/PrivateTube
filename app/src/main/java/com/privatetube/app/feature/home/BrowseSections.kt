package com.privatetube.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.Text
import com.privatetube.app.ui.Thumb
import com.privatetube.app.R
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.domain.CategoryItem
import com.privatetube.app.domain.ChannelItem
import com.privatetube.app.domain.Page
import com.privatetube.app.domain.StarItem
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.ListState
import com.privatetube.app.feature.PagedViewModel
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.FocusCard
import com.privatetube.app.ui.PagedGrid
import com.privatetube.app.ui.VideoCard

@Composable
fun CategoriesSection(onOpenList: (String, String) -> Unit, requestFocusOnEnter: Boolean) {
    val app = rememberApp()
    val vm: PagedViewModel<CategoryItem> = viewModel(
        key = "categories-${SiteConfig.language}",
        factory = viewModelFactory {
            initializer {
                PagedViewModel<CategoryItem>({ it.id }) { page ->
                    // Categories come on one page; later pages are empty, which ends the list.
                    if (page == 1) Page(app.repository.categories(), 1, hasMore = false) else Page(emptyList(), page, false)
                }
            }
        },
    )
    val state by vm.state.collectAsState()
    PagedGrid(
        state = state, columns = 5, onLoadMore = vm::loadMore, onRetry = vm::retry,
        emptyText = stringResource(R.string.categories_empty), key = { it.id }, requestFocusOnEnter = requestFocusOnEnter, thumbOf = { it.thumbUrl }, permanentThumbs = true,
    ) { item, modifier, onFocused ->
        FocusCard(onClick = { onOpenList(item.name, item.path) }, modifier = modifier, onFocused = onFocused) {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
                Thumb(item.thumbUrl, Modifier.fillMaxSize(), permanent = true)
                Box(
                    Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC000000)))),
                )
                Column(Modifier.align(Alignment.BottomStart).padding(10.dp)) {
                    Text(item.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    item.videoCount?.let { Text(stringResource(R.string.videos_count, it), fontSize = 13.sp, color = AppColors.TextSecondary, maxLines = 1) }
                }
            }
        }
    }
}

@Composable
fun ChannelsSection(onOpenList: (String, String) -> Unit, requestFocusOnEnter: Boolean) {
    val app = rememberApp()
    val vm: PagedViewModel<ChannelItem> = viewModel(
        key = "channels-${SiteConfig.language}",
        factory = viewModelFactory { initializer { PagedViewModel<ChannelItem>({ it.slug }) { app.repository.channels(it) } } },
    )
    val state by vm.state.collectAsState()
    PagedGrid(
        state = state, columns = 3, onLoadMore = vm::loadMore, onRetry = vm::retry,
        emptyText = stringResource(R.string.channels_empty), key = { it.slug }, requestFocusOnEnter = requestFocusOnEnter, thumbOf = { it.coverUrl ?: it.avatarUrl }, permanentThumbs = true,
    ) { item, modifier, onFocused ->
        FocusCard(onClick = { onOpenList(item.name, item.path) }, modifier = modifier, onFocused = onFocused) {
            Column {
                Box(Modifier.fillMaxWidth().aspectRatio(16f / 6f).background(Color.Black)) {
                    // A wide cover fills the tile. Without one the square logo is shown whole, not cut to the tile.
                    Thumb(
                        item.coverUrl ?: item.avatarUrl,
                        if (item.coverUrl != null) Modifier.fillMaxSize() else Modifier.fillMaxSize().padding(8.dp),
                        contentScale = if (item.coverUrl != null) ContentScale.Crop else ContentScale.Fit,
                        permanent = true,
                    )
                }
                Column(Modifier.padding(12.dp)) {
                    Text(item.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val meta = listOfNotNull(item.subscribers, item.videos).joinToString(" · ")
                    if (meta.isNotEmpty()) Text(meta, fontSize = 16.sp, color = AppColors.TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun StarsSection(onOpenList: (String, String) -> Unit, requestFocusOnEnter: Boolean) {
    val app = rememberApp()
    val vm: PagedViewModel<StarItem> = viewModel(
        key = "stars-${SiteConfig.language}",
        factory = viewModelFactory { initializer { PagedViewModel<StarItem>({ it.slug }) { app.repository.stars(it) } } },
    )
    val state by vm.state.collectAsState()
    PagedGrid(
        state = state, columns = 5, onLoadMore = vm::loadMore, onRetry = vm::retry,
        emptyText = stringResource(R.string.stars_empty), key = { it.slug }, requestFocusOnEnter = requestFocusOnEnter, thumbOf = { it.thumbUrl }, permanentThumbs = true,
    ) { item, modifier, onFocused ->
        FocusCard(onClick = { onOpenList(item.name, item.path) }, modifier = modifier, onFocused = onFocused) {
            Column {
                Box(Modifier.fillMaxWidth().aspectRatio(1f).background(Color.Black)) {
                    Thumb(item.thumbUrl, Modifier.fillMaxSize(), permanent = true)
                }
                Text(
                    item.name, fontSize = 20.sp, color = AppColors.TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}

/** Calls [onEmptied] when the user removes the last entry: with nothing left to focus, the caller moves the focus. */
@Composable
fun OnListEmptied(size: Int, onEmptied: () -> Unit) {
    var hadItems by remember { mutableStateOf(false) }
    LaunchedEffect(size) {
        if (size > 0) {
            hadItems = true
        } else if (hadItems) {
            hadItems = false
            // After the removed card (and its menu) are gone, or the system would move the focus again.
            delay(250)
            onEmptied()
        }
    }
}

/** Local lists (favorites, history) have no loading step, so they are wrapped into a ready [ListState]. */
@Composable
private fun LocalVideoGrid(
    videos: List<VideoItem>, emptyText: String, onOpenVideo: (String) -> Unit, requestFocusOnEnter: Boolean, onEmptied: () -> Unit,
) {
    OnListEmptied(videos.size, onEmptied)
    val state = remember(videos) { ListState(items = videos, loading = false, endReached = true) }
    PagedGrid(
        state = state, columns = 3, onLoadMore = {}, onRetry = {},
        emptyText = emptyText, key = { it.viewKey }, requestFocusOnEnter = requestFocusOnEnter, thumbOf = { it.thumbUrl }, refocusOnRemoval = true,
    ) { item, modifier, onFocused ->
        VideoCard(item, onClick = { onOpenVideo(item.viewKey) }, modifier = modifier, onFocused = onFocused)
    }
}

@Composable
fun FavoritesSection(onOpenVideo: (String) -> Unit, requestFocusOnEnter: Boolean, onEmptied: () -> Unit) {
    val videos by rememberApp().userData.favorites.collectAsState(initial = emptyList())
    LocalVideoGrid(videos, stringResource(R.string.favorites_empty), onOpenVideo, requestFocusOnEnter, onEmptied)
}

@Composable
fun HistorySection(onOpenVideo: (String) -> Unit, requestFocusOnEnter: Boolean, onEmptied: () -> Unit) {
    val videos by rememberApp().userData.history.collectAsState(initial = emptyList())
    LocalVideoGrid(videos, stringResource(R.string.history_empty), onOpenVideo, requestFocusOnEnter, onEmptied)
}
