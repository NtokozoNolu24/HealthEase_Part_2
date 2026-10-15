package com.example.healthease.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Simple salted SHA-256 password hasher (PBKDF2-style loop).
 * For production, prefer a real bcrypt library per AUTH-001.
 */
object PasswordHasher {

    private const val SALT_LENGTH = 16
    private const val ITERATIONS = 10_000

    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password, salt, ITERATIONS)
        val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        return "$ITERATIONS:$saltB64:$hashB64"
    }

    fun verify(password: String, stored: String): Boolean {
        return try {
            val parts = stored.split(":")
            if (parts.size != 3) return false
            val iterations = parts[0].toInt()
            val salt = Base64.decode(parts[1], Base64.NO_WRAP)
            val expected = Base64.decode(parts[2], Base64.NO_WRAP)
            val actual = pbkdf2(password, salt, iterations)
            MessageDigest.isEqual(expected, actual)
        } catch (e: Exception) {
            false
        }
    }

    private fun pbkdf2(password: String, salt: ByteArray, iterations: Int): ByteArray {
        var result = password.toByteArray() + salt
        val md = MessageDigest.getInstance("SHA-256")
        repeat(iterations) {
            result = md.digest(result)
        }
        return result
    }
}