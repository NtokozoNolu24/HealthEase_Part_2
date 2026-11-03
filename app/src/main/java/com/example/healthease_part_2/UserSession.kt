
package com.example.healthease_part_2

import android.content.Context
import android.content.res.Configuration
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

/**
 * Stores the signed-in user's profile and application preferences.
 * Passwords are never stored here.
 */
object UserSession {

    private const val PREFS_NAME = "healthease_session"

    private const val KEY_NAME = "name"
    private const val KEY_EMAIL = "email"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_LOGGED_IN = "logged_in"

    private const val KEY_NOTIF_ENABLED = "notif_enabled"
    private const val KEY_MED_REMINDER_ENABLED = "med_reminder_enabled"
    private const val KEY_APP_REMINDER_ENABLED = "app_reminder_enabled"

    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_TEXT_SIZE = "text_size"

    val LANGUAGES = listOf(
        "English",
        "isiZulu",
        "isiXhosa",
        "Sesotho",
        "Setswana",
        "Sepedi",
        "Tshivenda",
        "itsonga",
        "siSwati",
        "isiNdebele",
        "Afrikaans",
        "French",
        "Portuguese",
        "Spanish"
    )

    val TEXT_SIZES = listOf(
        "Small",
        "Medium",
        "Large"
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Save user profile
    fun saveProfile(
        context: Context,
        name: String,
        email: String,
        language: String
    ) {
        prefs(context).edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    // Save application settings
    fun saveSettings(
        context: Context,
        language: String,
        notifEnabled: Boolean,
        medEnabled: Boolean,
        appEnabled: Boolean,
        darkMode: Boolean = isDarkModeEnabled(context),
        textSize: String = getTextSize(context)
    ) {
        prefs(context).edit()
            .putString(KEY_LANGUAGE, language)
            .putBoolean(KEY_NOTIF_ENABLED, notifEnabled)
            .putBoolean(KEY_MED_REMINDER_ENABLED, medEnabled)
            .putBoolean(KEY_APP_REMINDER_ENABLED, appEnabled)
            .putBoolean(KEY_DARK_MODE, darkMode)
            .putString(KEY_TEXT_SIZE, textSize)
            .apply()
    }

    // Log in user
    fun logIn(context: Context, email: String, name: String? = null) {
        val p = prefs(context)
        val editor = p.edit().putBoolean(KEY_LOGGED_IN, true)
        val savedEmail = p.getString(KEY_EMAIL, "") ?: ""

        if (!savedEmail.equals(email, ignoreCase = true)) {
            editor.putString(KEY_EMAIL, email)
            editor.putString(KEY_NAME, name ?: "")
            editor.putString(KEY_LANGUAGE, LANGUAGES[0])
            editor.putBoolean(KEY_NOTIF_ENABLED, true)
            editor.putBoolean(KEY_MED_REMINDER_ENABLED, true)
            editor.putBoolean(KEY_APP_REMINDER_ENABLED, true)
            editor.putBoolean(KEY_DARK_MODE, false)
            editor.putString(KEY_TEXT_SIZE, "Medium")
        } else if (!name.isNullOrEmpty()) {
            editor.putString(KEY_NAME, name)
        }

        editor.apply()
    }

    // Log out user from both local session and Firebase
    fun logOut(context: Context) {
        FirebaseAuth.getInstance().signOut()

        prefs(context).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .apply()
    }

    // Check local login status
    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    // Get Firebase authenticated user's ID
    fun getFirebaseUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // Check whether Firebase has an authenticated user
    fun isFirebaseAuthenticated(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    // Get user name
    fun getName(context: Context): String =
        prefs(context).getString(KEY_NAME, "") ?: ""

    // Get user email
    fun getEmail(context: Context): String =
        prefs(context).getString(KEY_EMAIL, "") ?: ""

    // Get selected language
    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, LANGUAGES[0]) ?: LANGUAGES[0]

    // Notification settings
    fun isNotificationsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIF_ENABLED, true)

    fun isMedicationRemindersEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_MED_REMINDER_ENABLED, true)

    fun isAppointmentRemindersEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_APP_REMINDER_ENABLED, true)

    // Appearance settings
    fun isDarkModeEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK_MODE, false)

    fun getTextSize(context: Context): String =
        prefs(context).getString(KEY_TEXT_SIZE, "Medium") ?: "Medium"

    // Display name
    fun displayName(context: Context): String {
        val name = getName(context)
        if (name.isNotEmpty()) return name

        val fromEmail = getEmail(context).substringBefore("@")
        return if (fromEmail.isNotEmpty()) fromEmail else "there"
    }

    // Apply selected application language
    fun applyLocale(context: Context): Context {

        val languageCode = when (getLanguage(context)) {
            "English" -> "en"
            "isiZulu" -> "zu"
            "isiXhosa" -> "xh"
            "Sesotho" -> "st"
            "Setswana" -> "tn"
            "Sepedi" -> "nso"
            "Tshivenda" -> "ve"
            "itsonga" -> "ts"
            "siSwati" -> "ss"
            "isiNdebele" -> "nr"
            "Afrikaans" -> "af"
            "French" -> "fr"
            "Portuguese" -> "pt"
            "Spanish" -> "es"
            else -> "en"
        }

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
