package com.privatetube.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.domain.Author
import com.privatetube.app.feature.ListState
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.FocusCard
import com.privatetube.app.ui.MenuDialog
import com.privatetube.app.ui.PagedGrid
import com.privatetube.app.ui.Thumb
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.launch

/** The authors the user is subscribed to. OK opens the author's videos; holding OK offers to unsubscribe. */
@Composable
fun SubscriptionsSection(onOpenList: (String, String) -> Unit, requestFocusOnEnter: Boolean, onEmptied: () -> Unit) {
    val app = rememberApp()
    val scope = rememberCoroutineScope()
    val authors by app.userData.subscriptions.collectAsState(initial = emptyList())
    var menuAuthor by remember { mutableStateOf<Author?>(null) }
    OnListEmptied(authors.size, onEmptied)
    val state = remember(authors) { ListState(items = authors, loading = false, endReached = true) }

    menuAuthor?.let { author ->
        val first = remember { FocusRequester() }
        MenuDialog(onDismiss = { menuAuthor = null }) {
            LaunchedEffect(Unit) { first.requestFocusWhenReady() }
            Text(
                author.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            ActionButton(
                stringResource(R.string.sub_open),
                { menuAuthor = null; onOpenList(author.name, author.path) },
                Modifier.fillMaxWidth(), focusRequester = first,
            )
            ActionButton(
                stringResource(R.string.sub_remove),
                { menuAuthor = null; scope.launch { app.userData.unsubscribe(author.path) } },
                Modifier.fillMaxWidth(),
            )
        }
    }

    PagedGrid(
        state = state, columns = 5, onLoadMore = {}, onRetry = {},
        emptyText = stringResource(R.string.subscriptions_empty), key = { it.path },
        requestFocusOnEnter = requestFocusOnEnter, thumbOf = { it.avatarUrl }, permanentThumbs = true,
        refocusOnRemoval = true,
    ) { author, modifier, onFocused ->
        FocusCard(
            onClick = { onOpenList(author.name, author.path) }, modifier = modifier, onFocused = onFocused,
            onLongClick = { menuAuthor = author },
        ) {
            Column {
                Box(Modifier.fillMaxWidth().aspectRatio(1f).background(Color.Black)) {
                    if (author.avatarUrl != null) {
                        Thumb(author.avatarUrl, Modifier.fillMaxSize(), permanent = true)
                    } else {
                        Text(
                            author.name.take(1).uppercase(), fontSize = 56.sp, fontWeight = FontWeight.Bold,
                            color = AppColors.Accent, modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
                Text(
                    author.name, fontSize = 20.sp, color = AppColors.TextPrimary, maxLines = 1,
                    overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(12.dp),
                )
            }
        }
    }
}