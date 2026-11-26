package com.privatetube.app.feature.home

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.core.network.SiteConfig
import com.privatetube.app.data.AppLanguage
import com.privatetube.app.data.FontSize
import com.privatetube.app.data.LanguagePrefs
import com.privatetube.app.feature.pin.PinPad
import com.privatetube.app.feature.pin.PinState
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.MenuDialog
import com.privatetube.app.ui.ScaledText
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.launch

private val QualityChoices = listOf(0, 1080, 720, 480, 240)

@Composable
private fun qualityLabel(height: Int) =
    if (height == 0) stringResource(R.string.quality_max) else stringResource(R.string.quality_upto, height)

@Composable
private fun fontSizeLabel(size: FontSize) = stringResource(
    when (size) {
        FontSize.Medium -> R.string.font_medium
        FontSize.Large -> R.string.font_large
    },
)

@Composable
private fun languageLabel(language: AppLanguage) =
    if (language == AppLanguage.Auto) stringResource(R.string.lang_auto) else language.ownName

/** The list of languages; the current one is marked and has the focus. */
@Composable
private fun LanguageDialog(current: AppLanguage, onPick: (AppLanguage) -> Unit, onDismiss: () -> Unit) {
    val selected = remember { FocusRequester() }
    MenuDialog(onDismiss) {
        LaunchedEffect(Unit) { selected.requestFocusWhenReady() }
        Text(stringResource(R.string.language_title), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
        AppLanguage.entries.forEach { option ->
            ActionButton(
                languageLabel(option) + if (option == current) "  ✓" else "",
                { onPick(option) },
                Modifier.fillMaxWidth(),
                focusRequester = if (option == current) selected else null,
            )
        }
    }
}

/** What can be done with an existing PIN: change it or turn it off. BACK closes the menu. */
@Composable
private fun PinMenuDialog(onChange: () -> Unit, onDisable: () -> Unit, onDismiss: () -> Unit) {
    val first = remember { FocusRequester() }
    MenuDialog(onDismiss) {
        LaunchedEffect(Unit) { first.requestFocusWhenReady() }
        Text(stringResource(R.string.pin_manage), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
        ActionButton(stringResource(R.string.pin_change), onChange, Modifier.fillMaxWidth(), focusRequester = first)
        ActionButton(stringResource(R.string.pin_disable), onDisable, Modifier.fillMaxWidth())
    }
}

/** What the PIN screens inside Settings are doing right now. */
private sealed interface PinStep {
    /** Ask for the current PIN, then continue with [then]. */
    data class Verify(val then: Then) : PinStep
    data object New : PinStep
    data class Confirm(val first: String) : PinStep
    enum class Then { Change, Disable }
}

@Composable
fun SettingsSection() {
    // After the language change the screen is re-created; focus goes back to the language button, not to nowhere.
    val languageFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (LanguagePrefs.justChanged) {
            LanguagePrefs.justChanged = false
            languageFocus.requestFocusWhenReady()
        }
    }
    val app = rememberApp()
    val scope = rememberCoroutineScope()
    val quality by app.userData.preferredQuality.collectAsState(initial = 720)
    val fontSize by app.userData.fontSize.collectAsState(initial = FontSize.Default)
    val pinRecord by app.userData.pinRecord.collectAsState(initial = "")
    val hasPin = pinRecord.isNotEmpty()

    val context = androidx.compose.ui.platform.LocalContext.current
    val language = remember { LanguagePrefs.get(app) }
    var choosingLanguage by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf<PinStep?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var managingPin by remember { mutableStateOf(false) }

    if (managingPin) {
        PinMenuDialog(
            onChange = { managingPin = false; step = PinStep.Verify(PinStep.Then.Change) },
            onDisable = { managingPin = false; step = PinStep.Verify(PinStep.Then.Disable) },
            onDismiss = { managingPin = false },
        )
    }

    if (choosingLanguage) {
        LanguageDialog(
            current = language,
            onPick = { picked ->
                choosingLanguage = false
                if (picked != language) {
                    LanguagePrefs.set(app, picked)
                    LanguagePrefs.justChanged = true
                    // The site's own titles follow the language too, so cached pages go.
                    SiteConfig.language = LanguagePrefs.tag(app)
                    app.repository.clearCache()
                    // Re-create the screen so every text is redrawn in the new language.
                    (context as? android.app.Activity)?.recreate()
                }
            },
            onDismiss = { choosingLanguage = false },
        )
    }

    val current = step
    if (current != null) {
        val wrongText = stringResource(R.string.pin_wrong)
        val mismatchText = stringResource(R.string.pin_mismatch)
        Dialog(
            onDismissRequest = { step = null; message = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
        ) {
        ScaledText { PinPad(
            title = stringResource(
                when (current) {
                    is PinStep.Verify -> R.string.pin_enter_current
                    PinStep.New -> R.string.pin_enter_new
                    is PinStep.Confirm -> R.string.pin_repeat_new
                },
            ),
            message = message,
            onCancel = { step = null; message = null },
            onComplete = { pin ->
                scope.launch {
                    when (current) {
                        is PinStep.Verify -> if (app.userData.checkPin(pin)) {
                            message = null
                            when (current.then) {
                                PinStep.Then.Change -> step = PinStep.New
                                PinStep.Then.Disable -> { app.userData.clearPin(); PinState.unlocked = true; step = null }
                            }
                        } else message = wrongText
                        PinStep.New -> { message = null; step = PinStep.Confirm(pin) }
                        is PinStep.Confirm -> if (pin == current.first) {
                            app.userData.setPin(pin)
                            PinState.unlocked = true
                            message = null
                            step = null
                        } else { message = mismatchText; step = PinStep.New }
                    }
                }
            },
            modifier = Modifier,
        ) }
        }
    }

    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 32.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.settings_title), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)

        // Two columns of buttons that grow with their text, so nothing overlaps at any font size.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.settings_quality, qualityLabel(quality)),
            onClick = {
                val next = QualityChoices[(QualityChoices.indexOf(quality).let { if (it < 0) 0 else it } + 1) % QualityChoices.size]
                scope.launch { app.userData.setPreferredQuality(next) }
            },
        )
        ActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.settings_font, fontSizeLabel(fontSize)),
            onClick = {
                val all = FontSize.entries
                scope.launch { app.userData.setFontSize(all[(fontSize.ordinal + 1) % all.size]) }
            },
        )
        ActionButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.settings_language, languageLabel(language)),
            onClick = { choosingLanguage = true },
            focusRequester = languageFocus,
        )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // One button in both states, so the two columns stay equal and the focus stays on it when the PIN appears
        // or disappears.
        ActionButton(
            stringResource(if (hasPin) R.string.pin_manage else R.string.pin_set),
            { if (hasPin) managingPin = true else step = PinStep.New },
            Modifier.fillMaxWidth(),
        )
        ActionButton(stringResource(R.string.settings_clear_history), { scope.launch { app.userData.clearHistory() } }, Modifier.fillMaxWidth())
        ActionButton(stringResource(R.string.settings_clear_cache), onClick = {
            app.repository.clearCache()
            app.clearAllImages()
            com.privatetube.app.ui.Thumbs.reset()
        }, modifier = Modifier.fillMaxWidth())
        }
        }
        BackupControls()

        Text(
            stringResource(R.string.about_title),
            fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary,
            // About one empty line between the buttons and this heading.
            modifier = Modifier.padding(top = 32.dp),
        )
        Text(
            stringResource(R.string.about_text),
            fontSize = 16.sp,
            color = AppColors.TextSecondary,
            // Focusable, so that with a large text size the remote can scroll down to the end of the text.
            modifier = Modifier.focusable(),
        )
    }
}
