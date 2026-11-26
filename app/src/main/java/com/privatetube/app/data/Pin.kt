package com.privatetube.app.data

import java.security.MessageDigest
import java.security.SecureRandom

/** The app's text size choices. [scale] multiplies every `sp` size of the layout. */
enum class FontSize(val id: Int, val scale: Float) {
    // New ids: the numbers 0..3 belonged to the earlier set of four sizes, and a saved one of those must not
    // silently turn into a different size.
    Medium(10, 0.76f),
    Large(11, 0.88f),
    ;

    companion object {
        val Default = Medium
        fun fromId(id: Int?): FontSize = entries.firstOrNull { it.id == id } ?: Default
    }
}

/**
 * PIN storage: a random salt plus the SHA-256 of salt+PIN, so the digits themselves are not kept on the device.
 * (A 4-digit PIN is only a casual lock against someone picking up the remote; it is not strong protection.)
 */
object Pin {
    const val LENGTH = 4

    fun record(pin: String): String {
        val salt = ByteArray(8).also { SecureRandom().nextBytes(it) }
        return hex(salt) + ":" + hex(hash(salt, pin))
    }

    fun matches(pin: String, record: String): Boolean {
        val parts = record.split(':')
        if (parts.size != 2) return false
        val salt = unhex(parts[0]) ?: return false
        return MessageDigest.isEqual(hash(salt, pin), unhex(parts[1]) ?: return false)
    }

    private fun hash(salt: ByteArray, pin: String): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(salt + pin.toByteArray(Charsets.UTF_8))

    private fun hex(bytes: ByteArray) = bytes.joinToString("") { "%02x".format(it) }

    private fun unhex(s: String): ByteArray? =
        if (s.length % 2 != 0 || s.isEmpty()) null
        else runCatching { ByteArray(s.length / 2) { s.substring(it * 2, it * 2 + 2).toInt(16).toByte() } }.getOrNull()
}
