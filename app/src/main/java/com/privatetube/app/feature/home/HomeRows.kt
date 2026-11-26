package com.privatetube.app.feature.home

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.ContentRepository
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.PagedViewModel
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.PagedGrid
import com.privatetube.app.ui.VideoCard
import java.util.concurrent.atomic.AtomicBoolean

/**
 * The home feed: the site's "Recommended" list as an endless vertical grid (no horizontal scrolling anywhere).
 * [PagedViewModel.refresh] reloads it from the start and bypasses the repository cache.
 */
fun feedViewModel(repo: ContentRepository): PagedViewModel<VideoItem> {
    val force = AtomicBoolean(false)
    return object : PagedViewModel<VideoItem>({ it.viewKey }, { page -> repo.recommended(page, force.getAndSet(false)) }) {
        override fun refresh() {
            force.set(true)
            super.refresh()
        }
    }
}

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun HomeFeedSection(onOpenVideo: (String) -> Unit, requestFocusOnEnter: Boolean) {
    val app = rememberApp()
    val vm = androidx.lifecycle.viewmodel.compose.viewModel<PagedViewModel<VideoItem>>(
        key = "feed-${SiteConfig.language}",
        factory = viewModelFactory { initializer { feedViewModel(app.repository) } },
    )
    val state by vm.state.collectAsState()
    val gridEntry = remember { FocusRequester() }

    // Coming in from the side menu focus goes straight to the videos, not to the refresh button above them.
    Column(Modifier.fillMaxSize().focusProperties { enter = { if (it == androidx.compose.ui.focus.FocusDirection.Right) gridEntry else FocusRequester.Default } }.focusGroup()) {
        Row(
            // The space below keeps the button clear of the cards that scroll out at the top edge of the grid.
            Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    stringResource(R.string.row_recommended),
                    fontSize = 30.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary,
                )
                Text(stringResource(R.string.vpn_notice), fontSize = 16.sp, color = AppColors.TextSecondary)
            }
            ActionButton(stringResource(R.string.refresh), vm::refresh)
        }
        PagedGrid(
            state = state, columns = 3, onLoadMore = vm::loadMore, onRetry = vm::retry,
            emptyText = stringResource(R.string.list_empty), key = { it.viewKey }, thumbOf = { it.thumbUrl },
            requestFocusOnEnter = requestFocusOnEnter,
            modifier = Modifier.focusRequester(gridEntry),
        ) { item, modifier, onFocused ->
            VideoCard(item, onClick = { onOpenVideo(item.viewKey) }, modifier = modifier, onFocused = onFocused)
        }
    }
}
