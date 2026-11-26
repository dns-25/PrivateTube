package com.privatetube.app.feature.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.privatetube.app.R
import com.privatetube.app.data.BackupCodec
import com.privatetube.app.data.BackupFiles
import com.privatetube.app.data.BackupFormatException
import com.privatetube.app.feature.rememberApp
import com.privatetube.app.ui.ActionButton
import com.privatetube.app.ui.AppColors
import com.privatetube.app.ui.MenuDialog
import com.privatetube.app.ui.requestFocusWhenReady
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

private const val MAX_LISTED = 12

private sealed interface BackupDialog {
    data class Message(val text: String) : BackupDialog
    data class Pick(val files: List<File>, val limitedAccess: Boolean) : BackupDialog
}

private enum class BackupAction { Export, Import }

/**
 * The two backup buttons of Settings (favorites, subscriptions and watch history). The app has its own list of files: many TV boxes have no system file picker.
 * Copies are written to the public "Download" folder; they are looked for there, in the root of the storage and on
 * USB drives.
 */
@Composable
fun BackupControls() {
    val app = rememberApp()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var dialog by remember { mutableStateOf<BackupDialog?>(null) }
    var waiting by remember { mutableStateOf<BackupAction?>(null) }

    fun export() {
        scope.launch {
            dialog = try {
                val data = app.userData.backupData()
                val file = withContext(Dispatchers.IO) {
                    BackupFiles.write(downloadFolder(), BackupFiles.name(Date()), BackupCodec.encode(data))
                }
                BackupDialog.Message(
                    context.getString(
                        R.string.backup_saved, file.name, data.favorites.size, data.subscriptions.size, data.history.size,
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                BackupDialog.Message(context.getString(R.string.backup_save_failed))
            }
        }
    }

    fun showCopies() {
        scope.launch {
            val files = withContext(Dispatchers.IO) { BackupFiles.find(searchFolders(context)).take(MAX_LISTED) }
            val limited = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()
            dialog = BackupDialog.Pick(files, limited)
        }
    }

    fun import(file: File) {
        dialog = null
        scope.launch {
            dialog = BackupDialog.Message(
                try {
                    val data = withContext(Dispatchers.IO) { BackupFiles.read(file) }
                    val added = app.userData.merge(data)
                    context.getString(R.string.backup_added, added.favorites, added.subscriptions, added.history)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: BackupFormatException) {
                    context.getString(R.string.backup_bad_file)
                } catch (e: Exception) {
                    context.getString(R.string.backup_read_failed)
                },
            )
        }
    }

    fun run(action: BackupAction) = when (action) {
        BackupAction.Export -> export()
        BackupAction.Import -> showCopies()
    }

    val storagePermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val action = waiting ?: return@rememberLauncherForActivityResult
        waiting = null
        if (result.values.all { it }) run(action)
        else dialog = BackupDialog.Message(context.getString(R.string.backup_denied))
    }

    // Android 10 and older ask for the storage permission; newer versions do not need it here.
    fun start(action: BackupAction) {
        val needed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            emptyList()
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .filter { context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        }
        if (needed.isEmpty()) {
            run(action)
        } else {
            waiting = action
            storagePermissions.launch(needed.toTypedArray())
        }
    }

    // Side by side: export closes the left column of Settings, import the right one (same gap as between the columns).
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        ActionButton(stringResource(R.string.backup_export), { start(BackupAction.Export) }, Modifier.weight(1f))
        ActionButton(stringResource(R.string.backup_import), { start(BackupAction.Import) }, Modifier.weight(1f))
    }

    val current = dialog ?: return
    // A new window for each step, so the first button of the step gets the focus.
    key(current) {
        val first = remember { FocusRequester() }
        MenuDialog(onDismiss = { dialog = null }) {
            LaunchedEffect(Unit) { first.requestFocusWhenReady() }
            val wide = Modifier.fillMaxWidth()
            when (current) {
                is BackupDialog.Message -> {
                    Text(current.text, fontSize = 18.sp, color = AppColors.TextPrimary)
                    ActionButton(stringResource(R.string.ok), { dialog = null }, wide, focusRequester = first)
                }
                is BackupDialog.Pick -> {
                    Text(
                        stringResource(if (current.files.isEmpty()) R.string.backup_none else R.string.backup_pick),
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary,
                    )
                    current.files.forEachIndexed { index, file ->
                        ActionButton(file.name, { import(file) }, wide, focusRequester = if (index == 0) first else null)
                    }
                    if (current.limitedAccess) {
                        Text(stringResource(R.string.backup_need_access), fontSize = 14.sp, color = AppColors.TextSecondary)
                        ActionButton(
                            stringResource(R.string.backup_allow_access),
                            {
                                dialog = if (openAllFilesAccess(context)) null
                                else BackupDialog.Message(context.getString(R.string.backup_no_settings))
                            },
                            wide, focusRequester = if (current.files.isEmpty()) first else null,
                        )
                    }
                    if (current.files.isEmpty() && !current.limitedAccess) {
                        ActionButton(stringResource(R.string.ok), { dialog = null }, wide, focusRequester = first)
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun downloadFolder(): File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

/** The "Download" folder and the root of the internal storage and of every USB drive / SD card. */
@Suppress("DEPRECATION")
private fun searchFolders(context: Context): List<File> {
    val primary = Environment.getExternalStorageDirectory()
    val roots = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.getSystemService(StorageManager::class.java).storageVolumes.mapNotNull { it.directory }
    } else {
        context.getExternalFilesDirs(null).filterNotNull().map { File(it.absolutePath.substringBefore("/Android/")) }
    }
    return listOf(downloadFolder()) +
        (listOf(primary) + roots).distinct().flatMap { listOf(it, File(it, Environment.DIRECTORY_DOWNLOADS)) }
}

/** Opens the system screen where the user grants "all files access"; false when the device has none. */
private fun openAllFilesAccess(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
    val own = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + context.packageName))
    val list = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
    return listOf(own, list).any { runCatching { context.startActivity(it) }.isSuccess }
}