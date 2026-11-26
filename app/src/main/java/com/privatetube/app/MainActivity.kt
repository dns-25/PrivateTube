package com.privatetube.app

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.privatetube.app.domain.VideoItem
import com.privatetube.app.feature.menu.VideoMenuDialog
import com.privatetube.app.ui.LocalFontScale
import com.privatetube.app.ui.LocalVideoMenu
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.privatetube.app.data.FontSize
import com.privatetube.app.feature.age.AgeGateScreen
import com.privatetube.app.feature.home.HomeScreen
import com.privatetube.app.feature.list.VideoListScreen
import com.privatetube.app.feature.pin.PinLockScreen
import com.privatetube.app.feature.pin.PinState
import com.privatetube.app.feature.player.PlayerScreen
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.AppTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private companion object {
        /** Screens of the app that exist right now (main thread only). */
        var alive = 0
        const val EXIT_DELAY_MS = 1_500L
    }

    // Every text of the app uses the language chosen in Settings (or the one derived from the system language).
    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(com.privatetube.app.data.LanguagePrefs.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alive++
        setContent { AppTheme { AppRoot(onExit = { finishAndRemoveTask() }) } }
    }

    override fun onDestroy() {
        super.onDestroy()
        alive--
        if (isFinishing) {
            // The user left with BACK: end the process too, so nothing of the app stays in memory. Not at once: when
            // the app is relaunched with a cleared task, the old screen is finished just before the new one appears
            // in the same process, and killing it then would close the new one as well.
            Handler(Looper.getMainLooper()).postDelayed(
                { if (alive == 0) android.os.Process.killProcess(android.os.Process.myPid()) },
                EXIT_DELAY_MS,
            )
        }
    }

    override fun onStop() {
        super.onStop()
        // Leaving the app (Home, TV off) locks it again when a PIN is set. A rotation/re-creation does not count.
        if (!isChangingConfigurations) {
            PinState.lock()
            // Leaving the app (BACK, HOME, TV off): previews of videos do not stay on the disk.
            (application as PrivateTubeApplication).clearVideoPreviews()
        }
    }
}

private object Routes {
    const val HOME = "home"
    const val LIST = "list/{title}/{path}"
    const val PLAYER = "player/{key}"

    fun list(title: String, path: String) = "list/${Uri.encode(title)}/${Uri.encode(path)}"
    fun player(key: String) = "player/$key"
}

@Composable
private fun AppRoot(onExit: () -> Unit) {
    val app = rememberApp()
    val scope = rememberCoroutineScope()
    // null = still reading the saved choice; nothing is shown (and nothing requested) until it is known.
    val confirmed by app.userData.ageConfirmed.map<Boolean, Boolean?> { it }.collectAsState(initial = null)
    val pinRecord by app.userData.pinRecord.map<String, String?> { it }.collectAsState(initial = null)
    val fontSize by app.userData.fontSize.map<FontSize, FontSize?> { it }.collectAsState(initial = null)

    val size = fontSize
    val pin = pinRecord
    val age = confirmed
    if (size == null || pin == null || age == null) return

    // The text size setting scales every sp value in the app.
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, size.scale),
        LocalFontScale provides size.scale,
    ) {
        when {
            pin.isNotEmpty() && !PinState.unlocked -> PinLockScreen(
                verify = { app.userData.checkPin(it) },
                onUnlocked = { PinState.unlocked = true },
                onExit = onExit,
            )
            !age -> AgeGateScreen(
                onConfirm = { scope.launch { app.userData.setAgeConfirmed(true) } },
                onExit = onExit,
            )
            else -> AppNav()
        }
    }
}

@Composable
private fun AppNav() {
    val nav = rememberNavController()
    // No transition animations: screens switch instantly (the default cross-fade makes the remote feel sluggish).
    // The video whose context menu (long press) is open.
    var menuVideo by remember { mutableStateOf<VideoItem?>(null) }
    menuVideo?.let { video ->
        VideoMenuDialog(
            video = video,
            onDismiss = { menuVideo = null },
            onPlay = { menuVideo = null; nav.navigate(Routes.player(video.viewKey)) },
            onOpenList = { title, path -> menuVideo = null; nav.navigate(Routes.list(title, path)) },
        )
    }
    val openMenu = remember { { video: VideoItem -> menuVideo = video } }
    CompositionLocalProvider(LocalVideoMenu provides openMenu) { NavHost(
        navController = nav,
        startDestination = Routes.HOME,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                // A video opens straight in the player: there is no intermediate details screen.
                onOpenVideo = { nav.navigate(Routes.player(it)) },
                onOpenList = { title, path -> nav.navigate(Routes.list(title, path)) },
            )
        }
        composable(
            Routes.LIST,
            arguments = listOf(navArgument("title") { type = NavType.StringType }, navArgument("path") { type = NavType.StringType }),
        ) { entry ->
            val title = Uri.decode(entry.arguments?.getString("title").orEmpty())
            val path = Uri.decode(entry.arguments?.getString("path").orEmpty())
            VideoListScreen(title, path, onOpenVideo = { nav.navigate(Routes.player(it)) }, onBack = { nav.popBackStack() })
        }
        composable(
            Routes.PLAYER,
            arguments = listOf(navArgument("key") { type = NavType.StringType }),
        ) { entry ->
            PlayerScreen(
                viewKey = entry.arguments?.getString("key").orEmpty(),
                onExit = { nav.popBackStack() },
                onOpenChannel = { title, path -> nav.navigate(Routes.list(title, path)) },
            )
        }
    } }
}
