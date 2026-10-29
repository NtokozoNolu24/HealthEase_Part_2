
package com.example.healthease_part_2

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AccountStore {

    private const val PREFS_NAME = "healthease_accounts"
    private const val KEY_ACCOUNTS = "accounts"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    data class Account(
        val name: String,
        val email: String,
        val language: String
    )

    sealed class RegistrationResult {
        data object Success : RegistrationResult()
        data object DuplicateEmail : RegistrationResult()
        data object StorageError : RegistrationResult()
    }

    sealed class LoginResult {
        data class Success(val account: Account) : LoginResult()
         object InvalidCredentials : LoginResult()
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun normaliseEmail(email: String) =
        email.trim().lowercase(java.util.Locale.ROOT)

    private fun deriveHash(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)

        return try {
            val factory = try {
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            } catch (_: java.security.NoSuchAlgorithmException) {
                // Compatibility fallback for older Android providers.
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            }

            factory.generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    private fun decode(value: String): ByteArray =
        Base64.decode(value, Base64.NO_WRAP)

    private fun readAccounts(context: Context): MutableMap<String, String> {
        val saved = preferences(context).getString(KEY_ACCOUNTS, null)
            ?: return mutableMapOf()

        return saved.split("\n")
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.split("|", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            .toMap()
            .toMutableMap()
    }

    private fun saveAccounts(
        context: Context,
        accounts: Map<String, String>
    ): Boolean {
        val saved = accounts.entries.joinToString("\n") {
            "${it.key}|${it.value}"
        }

        return preferences(context).edit()
            .putString(KEY_ACCOUNTS, saved)
            .commit()
    }

    fun register(
        context: Context,
        name: String,
        email: String,
        password: String,
        language: String
    ): RegistrationResult {
        val key = normaliseEmail(email)
        val accounts = readAccounts(context)

        if (accounts.containsKey(key)) {
            return RegistrationResult.DuplicateEmail
        }

        return try {
            val salt = ByteArray(SALT_LENGTH).also {
                SecureRandom().nextBytes(it)
            }

            val hash = deriveHash(password.toCharArray(), salt)

            val stored = listOf(
                Base64.encodeToString(
                    name.trim().toByteArray(Charsets.UTF_8),
                    Base64.NO_WRAP
                ),
                Base64.encodeToString(
                    language.toByteArray(Charsets.UTF_8),
                    Base64.NO_WRAP
                ),
                encode(salt),
                encode(hash)
            ).joinToString(":")

            accounts[key] = stored

            if (saveAccounts(context, accounts)) {
                RegistrationResult.Success
            } else {
                RegistrationResult.StorageError
            }
        } catch (_: Exception) {
            RegistrationResult.StorageError
        }
    }

    fun login(
        context: Context,
        email: String,
        password: String
    ): LoginResult {
        val key = normaliseEmail(email)
        val stored = readAccounts(context)[key]
            ?: return LoginResult.InvalidCredentials

        return try {
            val parts = stored.split(":", limit = 4)
            if (parts.size != 4) {
                return LoginResult.InvalidCredentials
            }

            val name = String(
                Base64.decode(parts[0], Base64.NO_WRAP),
                Charsets.UTF_8
            )
            val language = String(
                Base64.decode(parts[1], Base64.NO_WRAP),
                Charsets.UTF_8
            )
            val salt = decode(parts[2])
            val expectedHash = decode(parts[3])
            val actualHash = deriveHash(password.toCharArray(), salt)

            if (!MessageDigest.isEqual(expectedHash, actualHash)) {
                LoginResult.InvalidCredentials
            } else {
                LoginResult.Success(
                    Account(
                        name = name,
                        email = key,
                        language = language
                    )
                )
            }
        } catch (_: Exception) {
            LoginResult.InvalidCredentials
        }
    }
}
