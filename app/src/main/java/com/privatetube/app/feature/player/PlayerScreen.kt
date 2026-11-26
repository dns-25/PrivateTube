package com.privatetube.app.feature.player

import android.os.SystemClock
import android.view.KeyEvent as AndroidKeyEvent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.ContentRepository
import com.privatetube.app.data.UserDataStore
import com.privatetube.app.domain.StreamOption
import com.privatetube.app.domain.Unavailable
import com.privatetube.app.domain.VideoDetails
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.domain.chooseStream
import com.privatetube.app.feature.formatDuration
import com.privatetube.app.feature.formatMillis
import com.privatetube.app.feature.message
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.feature.userMessage
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.BACK_EXIT_WINDOW_MS
import com.privatetube.app.ui.ExitHint
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.ErrorView
import com.privatetube.app.ui.LoadingIndicator
import com.privatetube.app.ui.LoadingView
import com.privatetube.app.ui.ThinSpinner
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface PlayerUi {
    data object Loading : PlayerUi
    data class Error(val message: String) : PlayerUi
    data class Ready(val details: VideoDetails, val start: StreamOption, val resumeMs: Long) : PlayerUi
}

fun VideoDetails.toItem() = VideoItem(viewKey, title, thumbUrl, durationSeconds?.let(::formatDuration))

class PlayerViewModel(
    private val viewKey: String,
    private val repo: ContentRepository,
    private val user: UserDataStore,
) : ViewModel() {
    private val _ui = MutableStateFlow<PlayerUi>(PlayerUi.Loading)
    val ui: StateFlow<PlayerUi> = _ui.asStateFlow()

    init { load(force = false) }

    fun retry() = load(force = true)

    private fun load(force: Boolean) {
        viewModelScope.launch {
            _ui.value = PlayerUi.Loading
            try {
                val details = repo.videoDetails(viewKey, force)
                val start = chooseStream(details.streams, user.preferredQuality.first())
                if (details.unavailableReason != null || start == null) {
                    _ui.value = PlayerUi.Error((details.unavailableReason ?: Unavailable.NoStreams).message())
                } else {
                    val item = details.toItem()
                    user.addToHistory(item)
                    // The site signs thumbnail links for about a day; opening a video renews a stored favorite's picture.
                    if (details.thumbUrl != null) user.refreshStored(item)
                    _ui.value = PlayerUi.Ready(details, start, user.position(viewKey))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _ui.value = PlayerUi.Error(e.userMessage())
            }
        }
    }
}

@Composable
fun PlayerScreen(viewKey: String, onExit: () -> Unit, onOpenChannel: (title: String, path: String) -> Unit) {
    val app = rememberApp()
    val vm: PlayerViewModel = viewModel(
        key = "player:$viewKey",
        factory = viewModelFactory { initializer { PlayerViewModel(viewKey, app.repository, app.userData) } },
    )
    val ui by vm.ui.collectAsState()
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        when (val s = ui) {
            PlayerUi.Loading -> {
                BackHandler(onBack = onExit)
                LoadingView()
            }
            is PlayerUi.Error -> {
                BackHandler(onBack = onExit)
                ErrorView(s.message, vm::retry)
            }
            is PlayerUi.Ready -> PlayerContent(s, onExit, onOpenChannel)
        }
    }
}

private const val SEEK_STEP_MS = 30_000L
/**
 * A held LEFT/RIGHT repeats the step on the app's own clock: remotes send their key repeats at very different rates.
 * [SEEK_HOLD_DELAY_MS] keeps an ordinary press at a single step.
 */
private const val SEEK_HOLD_DELAY_MS = 300L
private const val SEEK_HOLD_STEP_MS = 110L
private const val SEEK_BAR_TIMEOUT_MS = 2_500L
private const val CONTROLS_TIMEOUT_MS = 4_000L
/** BACK has to be pressed twice within this time to leave the video. */

/** The state of an arrow that is being held down. Plain fields: nothing on screen reads them directly. */
private class HeldSeek {
    var keyCode = 0
    var targetMs: Long? = null
    var job: Job? = null
}

/** Hidden: nothing. Seek: only a thin progress line (after LEFT/RIGHT). Controls: the compact bottom panel (after OK/UP). */
private enum class Overlay { Hidden, Seek, Controls }

@OptIn(UnstableApi::class)
@Composable
private fun PlayerContent(
    ready: PlayerUi.Ready,
    onExit: () -> Unit,
    onOpenChannel: (title: String, path: String) -> Unit,
) {
    val context = LocalContext.current
    val app = rememberApp()
    val scope = rememberCoroutineScope()
    val details = ready.details

    val player = remember {
        val dataSource = OkHttpDataSource.Factory(app.siteClient.http)
            .setUserAgent(SiteConfig.UA_DESKTOP)
            .setDefaultRequestProperties(mapOf("Referer" to SiteConfig.BASE_URL + "/"))
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSource))
            .build()
    }

    val favorites by app.userData.favorites.collectAsState(initial = emptyList())
    val isFavorite = favorites.any { it.viewKey == details.viewKey }
    val subscriptions by app.userData.subscriptions.collectAsState(initial = emptyList())

    var quality by remember { mutableStateOf(ready.start) }
    var isPlaying by remember { mutableStateOf(false) }
    /** False until the first picture of the (re)loaded stream is on screen: this is the "Loading…" phase. */
    var started by remember { mutableStateOf(false) }
    /** The player is waiting for data. Shown as a spinner only; after the start there is no text for it. */
    var buffering by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var positionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var overlay by remember { mutableStateOf(Overlay.Hidden) }
    var interaction by remember { mutableIntStateOf(0) }
    var menuOpen by remember { mutableStateOf(false) }
    val rootFocus = remember { FocusRequester() }
    val menuFocus = remember { FocusRequester() }
    val errorFocus = remember { FocusRequester() }
    var lastBackAt by remember { mutableLongStateOf(0L) }
    var backHintAt by remember { mutableLongStateOf(0L) }

    val hold = remember { HeldSeek() }

    fun show(mode: Overlay) { overlay = mode; interaction++ }

    fun load(stream: StreamOption, at: Long, autoplay: Boolean = true) {
        error = null
        started = false
        buffering = true
        player.setMediaItem(
            MediaItem.Builder().setUri(stream.url).setMimeType(MimeTypes.APPLICATION_M3U8).build(),
            at,
        )
        player.prepare()
        player.playWhenReady = autoplay
    }

    // Player lifecycle: created once, released on exit; MediaSession lets the system show/control playback.
    DisposableEffect(player) {
        val session = MediaSession.Builder(context, player).build()
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) started = true
            }
            override fun onRenderedFirstFrame() { started = true }
            override fun onPlaybackStateChanged(state: Int) {
                buffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) durationMs = player.duration.coerceAtLeast(0)
                if (state == Player.STATE_ENDED) overlay = Overlay.Controls
            }
            override fun onPlayerError(e: PlaybackException) {
                error = context.getString(R.string.player_error, e.errorCodeName)
                overlay = Overlay.Controls
            }
        }
        player.addListener(listener)
        load(ready.start, ready.resumeMs)
        onDispose {
            // Save where the viewer stopped, then free the decoder.
            val pos = player.currentPosition
            val dur = player.duration.coerceAtLeast(0)
            CoroutineScope(Dispatchers.IO + NonCancellable).launch { app.userData.savePosition(details.viewKey, pos, dur) }
            player.removeListener(listener)
            session.release()
            player.release()
        }
    }

    // The key release never arrives if the app leaves the screen while an arrow is held.
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        hold.job?.cancel()
        hold.targetMs?.let { player.seekTo(it) }
        hold.job = null
        hold.targetMs = null
        hold.keyCode = 0
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { player.pause() }
    LaunchedEffect(Unit) { rootFocus.requestFocusWhenReady() }

    // Progress ticker (only while something shows it) + periodic position save.
    LaunchedEffect(player) {
        var sinceSave = 0
        while (true) {
            // During a held seek the line shows where the video will jump to.
            positionMs = hold.targetMs ?: player.currentPosition
            if (player.duration > 0) durationMs = player.duration
            delay(500)
            if (player.isPlaying && ++sinceSave >= 20) {
                sinceSave = 0
                app.userData.savePosition(details.viewKey, player.currentPosition, player.duration.coerceAtLeast(0))
            }
        }
    }
    // Auto-hide: the thin line quickly, the panel after a few seconds (and only while playing).
    LaunchedEffect(overlay, interaction, isPlaying, menuOpen) {
        if (menuOpen) return@LaunchedEffect
        when (overlay) {
            Overlay.Seek -> { delay(SEEK_BAR_TIMEOUT_MS); overlay = Overlay.Hidden }
            Overlay.Controls -> if (isPlaying) { delay(CONTROLS_TIMEOUT_MS); overlay = Overlay.Hidden }
            Overlay.Hidden -> Unit
        }
    }
    LaunchedEffect(menuOpen) { if (menuOpen) menuFocus.requestFocusWhenReady() else rootFocus.requestFocusWhenReady() }
    LaunchedEffect(error) { if (error != null) errorFocus.requestFocusWhenReady() else rootFocus.requestFocusWhenReady() }

    // BACK closes the menu first; otherwise the video is left only by pressing it twice in a row (with a hint).
    fun handleBack() {
        when {
            menuOpen -> menuOpen = false
            error != null -> onExit()
            // With the controls on screen the first BACK only hides them.
            overlay == Overlay.Controls -> { overlay = Overlay.Hidden; lastBackAt = 0L; backHintAt = 0L }
            else -> {
                val now = SystemClock.uptimeMillis()
                if (now - lastBackAt <= BACK_EXIT_WINDOW_MS) {
                    onExit()
                } else {
                    lastBackAt = now
                    backHintAt = now
                }
            }
        }
    }
    // The hint is drawn by the app itself (a system Toast carries the app icon on some TV versions).
    LaunchedEffect(backHintAt) {
        if (backHintAt != 0L) {
            kotlinx.coroutines.delay(BACK_EXIT_WINDOW_MS)
            backHintAt = 0L
        }
    }
    // The key itself is also taken in onPreviewKeyEvent below: when a button of the menu has focus, the first BACK
    // would otherwise only clear that focus and never reach this handler (the "press it twice" problem).
    BackHandler { handleBack() }

    fun targetFrom(fromMs: Long, deltaMs: Long): Long {
        val limit = if (player.duration > 0) player.duration else Long.MAX_VALUE
        return (fromMs + deltaMs).coerceIn(0L, limit)
    }
    // The thin line is enough while seeking; if the full panel is already up, it simply stays.
    fun showSeeking() = show(if (overlay == Overlay.Controls) Overlay.Controls else Overlay.Seek)
    fun seekBy(deltaMs: Long) {
        val target = targetFrom(player.currentPosition, deltaMs)
        player.seekTo(target)
        positionMs = target
        showSeeking()
    }
    /** Ends a held seek: only now is the stream asked for the new place, once instead of on every step. */
    fun finishHold() {
        hold.job?.cancel()
        hold.job = null
        hold.keyCode = 0
        hold.targetMs?.let { player.seekTo(it) }
        hold.targetMs = null
    }
    fun startHold(keyCode: Int, deltaMs: Long) {
        finishHold()
        seekBy(deltaMs)
        hold.keyCode = keyCode
        hold.job = scope.launch {
            delay(SEEK_HOLD_DELAY_MS)
            while (true) {
                val target = targetFrom(hold.targetMs ?: player.currentPosition, deltaMs)
                hold.targetMs = target
                positionMs = target
                showSeeking()
                delay(SEEK_HOLD_STEP_MS)
            }
        }
    }
    fun togglePlay() {
        if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
        if (player.isPlaying) player.pause() else player.play()
        show(Overlay.Controls)
    }

    Box(
        Modifier
            .fillMaxSize()
            .focusRequester(rootFocus)
            .onPreviewKeyEvent { event ->
                val code = event.nativeKeyEvent.keyCode
                if (code == AndroidKeyEvent.KEYCODE_BACK) {
                    if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0) handleBack()
                    return@onPreviewKeyEvent true
                }
                // Releasing the held arrow (or pressing anything else) ends the held seek.
                if (hold.keyCode != 0) {
                    val released = event.type == KeyEventType.KeyUp && code == hold.keyCode
                    if (released || (event.type == KeyEventType.KeyDown && code != hold.keyCode)) finishHold()
                }
                // The quality menu and the error panel have their own focusable buttons; leave keys to them.
                if (menuOpen || error != null) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent code in HANDLED_KEYS
                val repeat = event.nativeKeyEvent.repeatCount
                when (code) {
                    // OK first reveals the controls; pressing it again while they are up pauses/resumes.
                    AndroidKeyEvent.KEYCODE_DPAD_CENTER, AndroidKeyEvent.KEYCODE_ENTER, AndroidKeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        if (repeat == 0) { if (overlay == Overlay.Controls) togglePlay() else show(Overlay.Controls) }
                        true
                    }
                    AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { if (repeat == 0) togglePlay(); true }
                    AndroidKeyEvent.KEYCODE_MEDIA_PLAY -> { player.play(); show(Overlay.Controls); true }
                    AndroidKeyEvent.KEYCODE_MEDIA_PAUSE -> { player.pause(); show(Overlay.Controls); true }
                    AndroidKeyEvent.KEYCODE_DPAD_LEFT, AndroidKeyEvent.KEYCODE_DPAD_RIGHT,
                    AndroidKeyEvent.KEYCODE_MEDIA_REWIND, AndroidKeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                        // The system's own key repeats are ignored: the held seek has its own pace.
                        if (repeat == 0) {
                            val back = code == AndroidKeyEvent.KEYCODE_DPAD_LEFT || code == AndroidKeyEvent.KEYCODE_MEDIA_REWIND
                            startHold(code, if (back) -SEEK_STEP_MS else SEEK_STEP_MS)
                        }
                        true
                    }
                    AndroidKeyEvent.KEYCODE_MEDIA_STOP -> { onExit(); true }
                    // UP with the controls hidden shows them; UP with the controls shown opens the menu (quality etc.).
                    AndroidKeyEvent.KEYCODE_DPAD_UP -> {
                        if (repeat == 0) { if (overlay == Overlay.Controls) menuOpen = true else show(Overlay.Controls) }
                        true
                    }
                    AndroidKeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (repeat == 0) { if (overlay == Overlay.Controls) overlay = Overlay.Hidden else show(Overlay.Controls) }
                        true
                    }
                    else -> false
                }
            }
            .focusable(),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    keepScreenOn = true
                    setShutterBackgroundColor(android.graphics.Color.BLACK)
                    this.player = player
                }
            },
        )

        // Status in the middle of the screen: a thin spinner while loading or buffering. While the stream is loading
        // it also says so in words (unless the control panel is up: then the word sits next to the time).
        val loading = !started && error == null
        if ((loading || buffering) && error == null && !menuOpen) {
            LoadingIndicator(Modifier.align(Alignment.Center), showText = loading && overlay != Overlay.Controls)
        }

        if (backHintAt != 0L && !menuOpen) {
            ExitHint(Modifier.align(Alignment.BottomCenter))
        }

        error?.let { message ->
            Column(
                Modifier.align(Alignment.Center).background(Color(0xDD000000), RoundedCornerShape(16.dp)).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(message, fontSize = 24.sp, color = AppColors.Error, textAlign = TextAlign.Center)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionButton(stringResource(R.string.retry), { load(quality, player.currentPosition) }, focusRequester = errorFocus)
                    if (details.streams.size > 1) ActionButton(stringResource(R.string.player_other_quality), { menuOpen = true })
                    ActionButton(stringResource(R.string.player_back), onExit)
                }
            }
        }

        if (!menuOpen) {
            when (overlay) {
                Overlay.Hidden -> Unit
                Overlay.Seek -> ProgressLine(positionMs, durationMs, Modifier.align(Alignment.BottomCenter), height = 4)
                Overlay.Controls -> ControlsPanel(
                    title = (if (isFavorite) "★ " else "") + details.title,
                    playing = isPlaying,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    loadingText = if (loading) stringResource(R.string.loading) else null,
                    hint = stringResource(R.string.player_hint) + stringResource(R.string.player_hint_menu, quality.label),
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }

        if (menuOpen) {
            val author = details.author
            Column(
                Modifier
                    .align(Alignment.Center)
                    .background(Color(0xEE15171C), RoundedCornerShape(16.dp))
                    // With several qualities and a large text size the menu may be taller than the screen.
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (details.streams.size > 1) {
                    Text(stringResource(R.string.player_quality), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AppColors.Accent)
                    details.streams.forEach { stream ->
                        ActionButton(
                            text = stream.label + if (stream.height == quality.height) "  ✓" else "",
                            onClick = {
                                val at = player.currentPosition
                                quality = stream
                                menuOpen = false
                                load(stream, at)
                            },
                            focusRequester = if (stream.height == quality.height) menuFocus else null,
                        )
                    }
                }
                ActionButton(
                    text = stringResource(if (isFavorite) R.string.fav_remove else R.string.fav_add),
                    onClick = { scope.launch { app.userData.toggleFavorite(details.toItem()) } },
                    focusRequester = if (details.streams.size > 1) null else menuFocus,
                )
                if (author != null) {
                    val subscribed = subscriptions.any { it.path == author.path }
                    ActionButton(
                        stringResource(if (subscribed) R.string.sub_remove else R.string.sub_add),
                        { scope.launch { if (subscribed) app.userData.unsubscribe(author.path) else app.userData.subscribe(author) } },
                    )
                    ActionButton(stringResource(R.string.details_channel, author.name), { onOpenChannel(author.name, author.path) })
                }
            }
        }
    }
}

private val HANDLED_KEYS = setOf(
    AndroidKeyEvent.KEYCODE_DPAD_CENTER, AndroidKeyEvent.KEYCODE_ENTER, AndroidKeyEvent.KEYCODE_NUMPAD_ENTER,
    AndroidKeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, AndroidKeyEvent.KEYCODE_MEDIA_PLAY, AndroidKeyEvent.KEYCODE_MEDIA_PAUSE,
    AndroidKeyEvent.KEYCODE_DPAD_LEFT, AndroidKeyEvent.KEYCODE_DPAD_RIGHT, AndroidKeyEvent.KEYCODE_DPAD_UP,
    AndroidKeyEvent.KEYCODE_DPAD_DOWN, AndroidKeyEvent.KEYCODE_MEDIA_REWIND, AndroidKeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
    AndroidKeyEvent.KEYCODE_MEDIA_STOP,
)

/** A thin white progress line: where the playback is, nothing else. */
@Composable
private fun ProgressLine(positionMs: Long, durationMs: Long, modifier: Modifier = Modifier, height: Int) {
    val fraction = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    Box(modifier.fillMaxWidth().height(height.dp).background(Color(0x55FFFFFF))) {
        Box(Modifier.fillMaxWidth(fraction).height(height.dp).background(Color.White))
    }
}

/** Compact panel pinned to the bottom edge: title, thin progress line, time and a one-line hint. */
@Composable
private fun ControlsPanel(
    title: String,
    playing: Boolean,
    positionMs: Long,
    durationMs: Long,
    loadingText: String?,
    hint: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD000000))))
            .padding(start = 40.dp, end = 40.dp, top = 28.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
        ProgressLine(positionMs, durationMs, height = 4)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${if (playing) "▶" else "❚❚"}  ${formatMillis(positionMs)} / ${formatMillis(durationMs)}",
                    fontSize = 16.sp, color = Color.White,
                )
                // While the stream is still loading, the status sits right after the duration.
                if (loadingText != null) {
                    Text("   $loadingText", fontSize = 16.sp, color = AppColors.Accent)
                }
            }
            Text(hint, fontSize = 14.sp, color = AppColors.TextSecondary, maxLines = 1)
        }
    }
}

