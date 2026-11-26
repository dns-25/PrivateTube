package com.privatetube.app.feature.home

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import android.app.Activity
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.feature.search.SearchSection
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.ExitHint
import com.privatetube.app.ui.FocusCard
import com.privatetube.app.ui.rememberDoubleBack
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.delay

enum class Section(@StringRes val title: Int, @DrawableRes val icon: Int) {
    Home(R.string.nav_home, R.drawable.ic_nav_home),
    Categories(R.string.nav_categories, R.drawable.ic_nav_categories),
    Channels(R.string.nav_channels, R.drawable.ic_nav_channels),
    Stars(R.string.nav_stars, R.drawable.ic_nav_stars),
    Search(R.string.nav_search, R.drawable.ic_nav_search),
    Subscriptions(R.string.nav_subscriptions, R.drawable.ic_nav_subscriptions),
    Favorites(R.string.nav_favorites, R.drawable.ic_nav_favorites),
    History(R.string.nav_history, R.drawable.ic_nav_history),
    Settings(R.string.nav_settings, R.drawable.ic_nav_settings),
}

/** Width of the collapsed (icons only) rail; the content always keeps this much room on the left. */
private val RailCollapsedWidth = 76.dp

/**
 * Side menu + content. While the menu has focus it shows icons with labels, as wide as its longest label, drawn
 * over the content; once focus enters the content it shrinks to a narrow icons-only strip. The content never
 * changes size, so there is no relayout while moving between the two.
 * A section opens only when its menu item is clicked. The app starts in the content, with the menu collapsed;
 * LEFT or BACK lead from the content to the menu item of the open section, RIGHT back into the content.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    onOpenVideo: (viewKey: String) -> Unit,
    onOpenList: (title: String, path: String) -> Unit,
) {
    /** The open section. It changes only when a menu item is clicked, never just because the item got focus. */
    var selected by rememberSaveable { mutableStateOf(Section.Home) }
    var contentHasFocus by remember { mutableStateOf(false) }
    var railHasFocus by remember { mutableStateOf(false) }
    // The content takes the focus: at the start (the menu stays collapsed) and after a return from a child screen
    // (list, player) or a re-creation, on the item that was focused before.
    var enterContent by remember { mutableStateOf(true) }
    val exit = rememberDoubleBack()
    // Counts menu clicks that must move focus into sections without a grid (search, settings).
    var enterRequests by remember { mutableIntStateOf(0) }
    val activity = LocalContext.current as? Activity

    val railRequesters = remember { List(Section.entries.size) { FocusRequester() } }
    val contentEntry = remember { FocusRequester() }

    LaunchedEffect(enterRequests) {
        if (enterRequests > 0) {
            withFrameNanos { }
            contentEntry.requestFocusWhenReady()
        }
    }
    /** Back to the menu item of the open section. */
    fun focusMenu() {
        enterContent = false
        runCatching { railRequesters[selected.ordinal].requestFocus() }
    }
    // BACK goes to the menu; pressed twice in the menu it closes the app completely.
    fun handleBack() {
        when {
            !railHasFocus -> { exit.reset(); focusMenu() }
            exit.press() -> activity?.finishAndRemoveTask()
        }
    }
    BackHandler { handleBack() }
    Box(
        Modifier.fillMaxSize().background(AppColors.Background)
            // Taken here as well: with a focused element the first BACK would otherwise only clear the focus.
            .onPreviewKeyEvent { event ->
                if (event.nativeKeyEvent.keyCode != android.view.KeyEvent.KEYCODE_BACK) return@onPreviewKeyEvent false
                if (event.type == KeyEventType.KeyDown && event.nativeKeyEvent.repeatCount == 0) handleBack()
                true
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = RailCollapsedWidth)
                .focusRequester(contentEntry)
                .onFocusChanged { contentHasFocus = it.hasFocus }
                .focusRestorer()
                .focusGroup(),
        ) {
            val enter = enterContent
            when (selected) {
                Section.Home -> HomeFeedSection(onOpenVideo, enter)
                Section.Categories -> CategoriesSection(onOpenList, enter)
                Section.Channels -> ChannelsSection(onOpenList, enter)
                Section.Stars -> StarsSection(onOpenList, enter)
                Section.Search -> SearchSection(onOpenVideo, enter)
                Section.Subscriptions -> SubscriptionsSection(onOpenList, enter, ::focusMenu)
                Section.Favorites -> FavoritesSection(onOpenVideo, enter, ::focusMenu)
                Section.History -> HistorySection(onOpenVideo, enter, ::focusMenu)
                Section.Settings -> SettingsSection()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(IntrinsicSize.Max)
                .background(Color(0xFF14161B))
                .padding(vertical = 6.dp, horizontal = 10.dp)
                .onFocusChanged {
                    if (!it.hasFocus) exit.reset()
                    // Focus has just come to the menu: a list that finishes loading now must not take it away.
                    if (it.hasFocus && !railHasFocus) enterContent = false
                    railHasFocus = it.hasFocus
                }
                // Moving around the menu: the content must not pull the focus back by itself any more.
                .onPreviewKeyEvent {
                    if (it.key == Key.DirectionUp || it.key == Key.DirectionDown) enterContent = false
                    false
                }
                .focusGroup(),
            // Nine items have to fit with the largest text and the taller Japanese and Chinese lines too.
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        ) {
            Section.entries.forEach { section ->
                val isSelected = section == selected
                FocusCard(
                    onClick = {
                        if (section == selected) {
                            runCatching { contentEntry.requestFocus() }
                        } else {
                            // Grids take the focus themselves once their items are there.
                            enterContent = true
                            selected = section
                            if (section == Section.Search || section == Section.Settings) enterRequests++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(railRequesters[section.ordinal])
                        // Coming from the content only the item of the open section can be reached, so LEFT always
                        // lands on it and not on whichever item happens to be nearest.
                        .focusProperties {
                            canFocus = railHasFocus || section == selected
                            right = contentEntry
                        },

                    shape = RoundedCornerShape(10.dp),
                    containerColor = if (isSelected) Color(0xFF23262D) else Color.Transparent,
                    focusedContainerColor = AppColors.Accent,
                ) { focused ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Image(
                            painter = painterResource(section.icon),
                            contentDescription = stringResource(section.title),
                            colorFilter = ColorFilter.tint(if (focused) AppColors.OnAccent else AppColors.Accent),
                        )
                        if (railHasFocus) {
                            Spacer(Modifier.width(16.dp))
                            Text(
                                stringResource(section.title),
                                fontSize = 22.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (focused) AppColors.OnAccent else if (isSelected) AppColors.Accent else AppColors.TextPrimary,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }

        if (exit.hintAt != 0L) ExitHint(Modifier.align(Alignment.BottomCenter))
    }
}
