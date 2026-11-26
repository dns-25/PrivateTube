package com.privatetube.app.feature.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.domain.Author
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.feature.userMessage
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.MenuDialog
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** A list item does not say who uploaded the video, so the menu reads it from the video page when it opens. */
private sealed interface AuthorState {
    data object Loading : AuthorState
    data class Ready(val author: Author?) : AuthorState
    data class Failed(val message: String) : AuthorState
}

/** A button that needs the author, pressed before the author is known. */
private enum class AuthorAction { Subscribe, OpenVideos }

/** The context menu of a video card. Every item works with the app's own data; nothing needs a site account. */
@Composable
fun VideoMenuDialog(
    video: VideoItem,
    onDismiss: () -> Unit,
    onPlay: () -> Unit,
    onOpenList: (title: String, path: String) -> Unit,
) {
    val app = rememberApp()
    val scope = rememberCoroutineScope()
    val favorites by app.userData.favorites.collectAsState(initial = emptyList())
    val history by app.userData.history.collectAsState(initial = emptyList())
    val subscriptions by app.userData.subscriptions.collectAsState(initial = emptyList())
    val isFavorite = favorites.any { it.viewKey == video.viewKey }
    val inHistory = history.any { it.viewKey == video.viewKey }
    // null until read: the first item must not change its name under the user's finger.
    var hasPosition by remember(video.viewKey) { mutableStateOf<Boolean?>(null) }
    var author by remember(video.viewKey) { mutableStateOf<AuthorState>(AuthorState.Loading) }
    var attempt by remember(video.viewKey) { mutableIntStateOf(0) }
    var wanted by remember(video.viewKey) { mutableStateOf<AuthorAction?>(null) }
    val first = remember { FocusRequester() }

    LaunchedEffect(video.viewKey) { hasPosition = app.userData.position(video.viewKey) > 0 }
    LaunchedEffect(video.viewKey, attempt) {
        author = AuthorState.Loading
        author = try {
            AuthorState.Ready(app.repository.videoDetails(video.viewKey, force = attempt > 0).author)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            wanted = null
            AuthorState.Failed(e.userMessage())
        }
    }
    // A press made during the loading is carried out as soon as the author arrives (closing the menu cancels it).
    LaunchedEffect(wanted, author) {
        val action = wanted ?: return@LaunchedEffect
        val found = (author as? AuthorState.Ready ?: return@LaunchedEffect).author
        if (found == null) {
            wanted = null
            return@LaunchedEffect
        }
        when (action) {
            // "Subscribe" was what the button said, so an author who is already in the list stays there.
            AuthorAction.Subscribe -> scope.launch { app.userData.subscribe(found); onDismiss() }
            AuthorAction.OpenVideos -> onOpenList(found.name, found.path)
        }
    }

    val resume = hasPosition ?: return
    MenuDialog(onDismiss) {
        LaunchedEffect(Unit) { first.requestFocusWhenReady() }
        Text(
            video.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
        )
        val state = author
        val known = (state as? AuthorState.Ready)?.author
        Text(
            when {
                state is AuthorState.Loading -> stringResource(R.string.menu_author_loading)
                state is AuthorState.Failed -> state.message
                known != null -> stringResource(R.string.menu_author, known.name)
                else -> stringResource(R.string.menu_no_channel)
            },
            fontSize = 16.sp,
            color = if (state is AuthorState.Failed) AppColors.Error else AppColors.TextSecondary,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
        )
        val wide = Modifier.fillMaxWidth()
        // Playback resumes from the saved position by itself, so the default item says exactly that.
        ActionButton(
            stringResource(if (resume) R.string.menu_continue else R.string.menu_watch),
            onPlay, wide, focusRequester = first,
        )
        if (resume) {
            ActionButton(stringResource(R.string.menu_from_start), {
                scope.launch { app.userData.forgetPosition(video.viewKey); onPlay() }
            }, wide)
        }
        ActionButton(stringResource(if (isFavorite) R.string.fav_remove else R.string.fav_add), {
            scope.launch { app.userData.toggleFavorite(video); onDismiss() }
        }, wide)
        // The two buttons of the author are there from the start, the author is looked up behind them. They go away
        // only if the video page names nobody.
        if (state !is AuthorState.Ready || known != null) {
            val subscribed = known != null && subscriptions.any { it.path == known.path }
            fun askFor(action: AuthorAction) {
                wanted = action
                // After a failed lookup the press tries once more.
                if (author is AuthorState.Failed) attempt++
            }
            val waiting = if (state is AuthorState.Ready) null else wanted
            ActionButton(
                stringResource(
                    when {
                        waiting == AuthorAction.Subscribe -> R.string.loading
                        subscribed -> R.string.sub_remove
                        else -> R.string.sub_add
                    },
                ),
                {
                    if (known == null) {
                        askFor(AuthorAction.Subscribe)
                    } else {
                        scope.launch {
                            if (subscribed) app.userData.unsubscribe(known.path) else app.userData.subscribe(known)
                            onDismiss()
                        }
                    }
                },
                wide,
            )
            ActionButton(
                stringResource(if (waiting == AuthorAction.OpenVideos) R.string.loading else R.string.menu_channel),
                { if (known == null) askFor(AuthorAction.OpenVideos) else onOpenList(known.name, known.path) },
                wide,
            )
        } else {
            // The focus may have been on a button that has just gone.
            LaunchedEffect(Unit) { first.requestFocusWhenReady() }
        }
        if (inHistory) {
            ActionButton(stringResource(R.string.menu_remove_history), {
                scope.launch { app.userData.removeFromHistory(video.viewKey); onDismiss() }
            }, wide)
        }
    }
}