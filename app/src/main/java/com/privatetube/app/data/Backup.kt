package com.privatetube.app.data

import com.privatetube.app.domain.Author
import com.privatetube.app.domain.VideoItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Serializable
data class BackupData(
    val favorites: List<VideoItem> = emptyList(),
    val subscriptions: List<Author> = emptyList(),
    /** Newest first. */
    val history: List<VideoItem> = emptyList(),
    /** Where playback of a video stopped, in milliseconds, by view key. */
    val positions: Map<String, Long> = emptyMap(),
    val version: Int = 1,
)

/** How many entries an import really added. */
data class MergeResult(val favorites: Int, val subscriptions: Int, val history: Int)

/** The file is not a backup of this app, or it is damaged. */
class BackupFormatException(cause: Throwable? = null) : Exception("not a backup file", cause)

/**
 * The file looks like random bytes: no readable titles, links or headers (compressed, then AES-GCM).
 * The key is built into the app, so this hides the content from a casual look; it is NOT password protection.
 */
object BackupCodec {
    private const val IV_LENGTH = 12
    private const val TAG_BITS = 128
    /** Far above any real backup; guards against reading or unpacking something huge by mistake. */
    const val MAX_BYTES = 8 * 1024 * 1024

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val key by lazy {
        SecretKeySpec(MessageDigest.getInstance("SHA-256").digest("pt.configuration.v1".toByteArray()), "AES")
    }

    fun encode(data: BackupData, random: SecureRandom = SecureRandom()): ByteArray {
        val packed = ByteArrayOutputStream().also { out ->
            GZIPOutputStream(out).use { it.write(json.encodeToString(BackupData.serializer(), data).toByteArray()) }
        }.toByteArray()
        val iv = ByteArray(IV_LENGTH).also(random::nextBytes)
        return iv + cipher(Cipher.ENCRYPT_MODE, iv).doFinal(packed)
    }

    fun decode(bytes: ByteArray): BackupData = try {
        require(bytes.size in (IV_LENGTH + 1)..MAX_BYTES)
        val packed = cipher(Cipher.DECRYPT_MODE, bytes.copyOf(IV_LENGTH))
            .doFinal(bytes, IV_LENGTH, bytes.size - IV_LENGTH)
        val text = GZIPInputStream(ByteArrayInputStream(packed)).use { input ->
            val out = ByteArrayOutputStream()
            val buffer = ByteArray(16 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                out.write(buffer, 0, read)
                require(out.size() <= MAX_BYTES)
            }
            out.toString("UTF-8")
        }
        json.decodeFromString(BackupData.serializer(), text)
    } catch (e: Exception) {
        throw BackupFormatException(e)
    }

    private fun cipher(mode: Int, iv: ByteArray) = Cipher.getInstance("AES/GCM/NoPadding").apply {
        init(mode, key, GCMParameterSpec(TAG_BITS, iv))
    }
}

/** Naming, writing and finding backup files. Plain files, so the user can copy them with any file manager. */
object BackupFiles {
    private const val PREFIX = "user_preferences"
    private const val EXTENSION = ".dat"
    /** Names written by earlier builds: not created any more, but still offered for import. */
    private const val OLD_PREFIX = "pt_configuration"
    private const val OLD_EXTENSION = ".cfg"

    /** E.g. `user_preferences_26_11_2026_02_49.dat`: looks like an ordinary service file. */
    fun name(date: Date): String =
        PREFIX + "_" + SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.US).format(date) + EXTENSION

    /** Writes a new file and never overwrites: a second export in the same minute gets a number. */
    fun write(directory: File, fileName: String, bytes: ByteArray): File {
        directory.mkdirs()
        var failure: Exception? = null
        for (attempt in 1..20) {
            val candidate = if (attempt == 1) fileName
            else fileName.removeSuffix(EXTENSION) + "_" + attempt + EXTENSION
            val file = File(directory, candidate)
            try {
                if (file.createNewFile()) {
                    file.writeBytes(bytes)
                    return file
                }
            } catch (e: Exception) {
                // On new Android a file left by a previous installation is invisible but still occupies the name.
                failure = e
            }
        }
        throw java.io.IOException("cannot create $fileName", failure)
    }

    fun isBackupName(name: String): Boolean =
        matches(name, PREFIX, EXTENSION) || matches(name, OLD_PREFIX, OLD_EXTENSION)

    private fun matches(name: String, prefix: String, extension: String) =
        name.startsWith(prefix, ignoreCase = true) && name.endsWith(extension, ignoreCase = true)

    /** All backups in the given folders, newest first. Unreadable folders are skipped. */
    fun find(directories: List<File>): List<File> = directories
        .flatMap { dir -> dir.listFiles()?.filter { it.isFile && isBackupName(it.name) }.orEmpty() }
        .distinctBy { it.absolutePath }
        .sortedWith(compareByDescending<File> { it.lastModified() }.thenByDescending { it.name })

    fun read(file: File): BackupData {
        if (file.length() > BackupCodec.MAX_BYTES) throw BackupFormatException()
        return BackupCodec.decode(file.readBytes())
    }
}