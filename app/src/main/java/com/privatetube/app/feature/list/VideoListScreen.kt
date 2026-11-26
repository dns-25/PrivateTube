package com.privatetube.app.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.PagedViewModel
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.PagedGrid
import com.privatetube.app.ui.VideoCard

/** Full-screen paged catalog of any video list of the site (a category, channel, model...): three cards per row, endless. */
@Composable
fun VideoListScreen(title: String, path: String, onOpenVideo: (String) -> Unit, onBack: () -> Unit) {
    val app = rememberApp()
    val vm: PagedViewModel<VideoItem> = viewModel(
        key = "list:$path",
        factory = viewModelFactory { initializer { PagedViewModel<VideoItem>({ it.viewKey }) { app.repository.videos(path, it) } } },
    )
    val state by vm.state.collectAsState()

    Column(
        Modifier.fillMaxSize().background(AppColors.Background)
            // One press goes back (otherwise the first BACK may only clear the focus of the card).
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.keyCode != android.view.KeyEvent.KEYCODE_BACK) return@onPreviewKeyEvent false
                if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0) onBack()
                true
            },
    ) {
        Text(
            title,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(start = 40.dp, top = 28.dp, end = 40.dp),
        )
        PagedGrid(
            state = state, columns = 3, onLoadMore = vm::loadMore, onRetry = vm::retry,
            emptyText = stringResource(R.string.list_empty), key = { it.viewKey }, thumbOf = { it.thumbUrl },
        ) { item, modifier, onFocused ->
            VideoCard(item, onClick = { onOpenVideo(item.viewKey) }, modifier = modifier, onFocused = onFocused)
        }
    }
}
