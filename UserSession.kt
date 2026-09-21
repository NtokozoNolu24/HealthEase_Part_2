package com.example.healthease_part_2

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Keeps the signed-in user's profile on the device (SharedPreferences).
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

    val LANGUAGES = arrayOf("English", "isiZulu")

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Called after registration and when the user edits their profile. */
    fun saveProfile(context: Context, name: String, email: String, language: String) {
        prefs(context).edit()
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_LANGUAGE, language)
            .apply()
    }

    fun saveSettings(
        context: Context,
        language: String,
        notifEnabled: Boolean,
        medEnabled: Boolean,
        appEnabled: Boolean
    ) {
        prefs(context).edit()
            .putString(KEY_LANGUAGE, language)
            .putBoolean(KEY_NOTIF_ENABLED, notifEnabled)
            .putBoolean(KEY_MED_REMINDER_ENABLED, medEnabled)
            .putBoolean(KEY_APP_REMINDER_ENABLED, appEnabled)
            .apply()
    }

    /** Called after a successful email/password or Google login. */
    fun logIn(context: Context, email: String, name: String? = null) {
        val p = prefs(context)
        val editor = p.edit().putBoolean(KEY_LOGGED_IN, true)
        val savedEmail = p.getString(KEY_EMAIL, "") ?: ""

        if (!savedEmail.equals(email, ignoreCase = true)) {
            // A different account from the one saved on this device
            editor.putString(KEY_EMAIL, email)
            editor.putString(KEY_NAME, name ?: "")
            editor.putString(KEY_LANGUAGE, LANGUAGES[0])
            editor.putBoolean(KEY_NOTIF_ENABLED, true)
            editor.putBoolean(KEY_MED_REMINDER_ENABLED, true)
            editor.putBoolean(KEY_APP_REMINDER_ENABLED, true)
        } else if (!name.isNullOrEmpty()) {
            editor.putString(KEY_NAME, name)
        }
        editor.apply()
    }

    fun logOut(context: Context) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun getName(context: Context): String =
        prefs(context).getString(KEY_NAME, "") ?: ""

    fun getEmail(context: Context): String =
        prefs(context).getString(KEY_EMAIL, "") ?: ""

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, LANGUAGES[0]) ?: LANGUAGES[0]

    fun isNotificationsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIF_ENABLED, true)

    fun isMedicationRemindersEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_MED_REMINDER_ENABLED, true)

    fun isAppointmentRemindersEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_APP_REMINDER_ENABLED, true)

    /** Name if we have one, otherwise the part of the email before the @. */
    fun displayName(context: Context): String {
        val name = getName(context)
        if (name.isNotEmpty()) return name
        val fromEmail = getEmail(context).substringBefore("@")
        return if (fromEmail.isNotEmpty()) fromEmail else "there"
    }

    fun applyLocale(context: Context): Context {
        val lang = getLanguage(context)
        val locale = if (lang == "isiZulu") Locale("zu") else Locale("en")
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
