package com.privatetube.app.feature.age

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.ExitHint
import com.privatetube.app.ui.onBackKey
import com.privatetube.app.ui.rememberDoubleBack
import com.privatetube.app.ui.requestFocusWhenReady

/**
 * The app's own age confirmation. Nothing from the site is requested before the viewer confirms it.
 * The default focus is on "exit", so content cannot be opened by an accidental press. Both buttons look the
 * same; the focused one is highlighted in orange.
 */
@Composable
fun AgeGateScreen(onConfirm: () -> Unit, onExit: () -> Unit) {
    val exitRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { exitRequester.requestFocusWhenReady() }
    // The exit button leaves at once; the BACK key asks to be pressed twice, like everywhere in the app.
    val back = rememberDoubleBack()
    fun handleBack() { if (back.press()) onExit() }
    BackHandler { handleBack() }

    Box(Modifier.fillMaxSize().onBackKey(::handleBack).padding(64.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = 900.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Text(stringResource(R.string.age_title), fontSize = 44.sp, fontWeight = FontWeight.Bold, color = AppColors.Accent)
            Text(
                stringResource(R.string.age_body),
                fontSize = 24.sp,
                lineHeight = 31.sp,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.age_note),
                fontSize = 18.sp,
                color = AppColors.TextSecondary,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ActionButton(stringResource(R.string.age_exit), onExit, focusRequester = exitRequester)
                ActionButton(stringResource(R.string.age_enter), onConfirm)
            }
        }
    }
    if (back.hintAt != 0L) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) { ExitHint() }
    }
}
