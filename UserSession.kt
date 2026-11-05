**package com.example.healthease_part_2**\
\
**import android.content.Context**\
**import android.content.res.Configuration**\
**import com.google.firebase.auth.FirebaseAuth**\
**import java.util.Locale**\
\
**/\*\***\&#xA;**&#x20;\* Stores the signed-in user's profile and application preferences.**\&#xA;**&#x20;\* Passwords are never stored here.**\&#xA;**&#x20;\*/**\
**object UserSession {**\
    \&#xA;**&#x20;   private const val PREFS_NAME = "healthease_session"**\
    \&#xA;**&#x20;   private const val KEY_NAME = "name"**\&#xA;**&#x20;   private const val KEY_EMAIL = "email"**\&#xA;**&#x20;   private const val KEY_LANGUAGE = "language"**\&#xA;**&#x20;   private const val KEY_LOGGED_IN = "logged_in"**\
    \&#xA;**&#x20;   private const val KEY_NOTIF_ENABLED = "notif_enabled"**\&#xA;**&#x20;   private const val KEY_MED_REMINDER_ENABLED = "med_reminder_enabled"**\&#xA;**&#x20;   private const val KEY_APP_REMINDER_ENABLED = "app_reminder_enabled"**\
    \&#xA;**&#x20;   private const val KEY_DARK_MODE = "dark_mode"**\&#xA;**&#x20;   private const val KEY_TEXT_SIZE = "text_size"**\
    \&#xA;**&#x20;   // Four supported application languages**\&#xA;**&#x20;   val LANGUAGES = listOf(**\&#xA;**&#x20;       "English",**\&#xA;**&#x20;       "isiZulu",**\&#xA;**&#x20;       "Afrikaans",**\&#xA;**&#x20;       "isiXhosa"**\&#xA;**&#x20;   )**\
    \&#xA;**&#x20;   val TEXT_SIZES = listOf(**\&#xA;**&#x20;       "Small",**\&#xA;**&#x20;       "Medium",**\&#xA;**&#x20;       "Large"**\&#xA;**&#x20;   )**\
    \&#xA;**&#x20;   private fun prefs(context: Context) =**\&#xA;**&#x20;       context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)**\
    \&#xA;**&#x20;   // Save user profile**\&#xA;**&#x20;   fun saveProfile(**\&#xA;**&#x20;       context: Context,**\&#xA;**&#x20;       name: String,**\&#xA;**&#x20;       email: String,**\&#xA;**&#x20;       language: String**\&#xA;**&#x20;   ) {**\&#xA;**&#x20;       prefs(context).edit()**\&#xA;**&#x20;           .putString(KEY_NAME, name)**\&#xA;**&#x20;           .putString(KEY_EMAIL, email)**\&#xA;**&#x20;           .putString(**\&#xA;**&#x20;               KEY_LANGUAGE,**\&#xA;**&#x20;               if (LANGUAGES.contains(language)) language else LANGUAGES[0]**\&#xA;**&#x20;           )**\&#xA;**&#x20;           .apply()**\&#xA;**&#x20;   }**\
    \&#xA;**&#x20;   // Save application settings**\&#xA;**&#x20;   fun saveSettings(**\&#xA;**&#x20;       context: Context,**\&#xA;**&#x20;       language: String,**\&#xA;**&#x20;       notifEnabled: Boolean,**\&#xA;**&#x20;       medEnabled: Boolean,**\&#xA;**&#x20;       appEnabled: Boolean,**\&#xA;**&#x20;       darkMode: Boolean = isDarkModeEnabled(context),**\&#xA;**&#x20;       textSize: String = getTextSize(context)**\&#xA;**&#x20;   ) {**\&#xA;**&#x20;       prefs(context).edit()**\&#xA;**&#x20;           .putString(**\&#xA;**&#x20;               KEY_LANGUAGE,**\&#xA;**&#x20;               if (LANGUAGES.contains(language)) language else LANGUAGES[0]**\&#xA;**&#x20;           )**\&#xA;**&#x20;           .putBoolean(KEY_NOTIF_ENABLED, notifEnabled)**\&#xA;**&#x20;           .putBoolean(KEY_MED_REMINDER_ENABLED, medEnabled)**\&#xA;**&#x20;           .putBoolean(KEY_APP_REMINDER_ENABLED, appEnabled)**\&#xA;**&#x20;           .putBoolean(KEY_DARK_MODE, darkMode)**\&#xA;**&#x20;           .putString(**\&#xA;**&#x20;               KEY_TEXT_SIZE,**\&#xA;**&#x20;               if (TEXT_SIZES.contains(textSize)) textSize else "Medium"**\&#xA;**&#x20;           )**\&#xA;**&#x20;           .apply()**\&#xA;**&#x20;   }**\
    \&#xA;**&#x20;   // Log in user**\&#xA;**&#x20;   fun logIn(context: Context, email: String, name: String? = null) {**\&#xA;**&#x20;       val p = prefs(context)**\&#xA;**&#x20;       val editor = p.edit().putBoolean(KEY_LOGGED_IN, true)**\&#xA;**&#x20;       val savedEmail = p.getString(KEY_EMAIL, "") ?: ""**\
    \&#xA;**&#x20;       if (!savedEmail.equals(email, ignoreCase = true)) {**\&#xA;**&#x20;           editor.putString(KEY_EMAIL, email)**\&#xA;**&#x20;           editor.putString(KEY_NAME, name ?: "")**\&#xA;**&#x20;           editor.putString(KEY_LANGUAGE, LANGUAGES[0])**\&#xA;**&#x20;           editor.putBoolean(KEY_NOTIF_ENABLED, true)**\&#xA;**&#x20;           editor.putBoolean(KEY_MED_REMINDER_ENABLED, true)**\&#xA;**&#x20;           editor.putBoolean(KEY_APP_REMINDER_ENABLED, true)**\&#xA;**&#x20;           editor.putBoolean(KEY_DARK_MODE, false)**\&#xA;**&#x20;           editor.putString(KEY_TEXT_SIZE, "Medium")**\&#xA;**&#x20;       } else if (!name.isNullOrEmpty()) {**\&#xA;**&#x20;           editor.putString(KEY_NAME, name)**\&#xA;**&#x20;       }**\
    \&#xA;**&#x20;       editor.apply()**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Log out user from both local session and Firebase**\&#xA;**&#x20;   fun logOut(context: Context) {**\&#xA;**&#x20;       FirebaseAuth.getInstance().signOut()**\
\&#xA;**&#x20;       prefs(context).edit()**\&#xA;**&#x20;           .putBoolean(KEY_LOGGED_IN, false)**\&#xA;**&#x20;           .apply()**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Check local login status**\&#xA;**&#x20;   fun isLoggedIn(context: Context): Boolean =**\&#xA;**&#x20;       prefs(context).getBoolean(KEY_LOGGED_IN, false)**\
\&#xA;**&#x20;   // Get Firebase authenticated user's ID**\&#xA;**&#x20;   fun getFirebaseUid(): String? {**\&#xA;**&#x20;       return FirebaseAuth.getInstance().currentUser?.uid**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Check whether Firebase has an authenticated user**\&#xA;**&#x20;   fun isFirebaseAuthenticated(): Boolean {**\&#xA;**&#x20;       return FirebaseAuth.getInstance().currentUser != null**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Get user name**\&#xA;**&#x20;   fun getName(context: Context): String =**\&#xA;**&#x20;       prefs(context).getString(KEY_NAME, "") ?: ""**\
\&#xA;**&#x20;   // Get user email**\&#xA;**&#x20;   fun getEmail(context: Context): String =**\&#xA;**&#x20;       prefs(context).getString(KEY_EMAIL, "") ?: ""**\
\&#xA;**&#x20;   // Get selected language**\&#xA;**&#x20;   fun getLanguage(context: Context): String {**\&#xA;**&#x20;       val savedLanguage = prefs(context).getString(KEY_LANGUAGE, LANGUAGES[0])**\&#xA;**&#x20;       return if (LANGUAGES.contains(savedLanguage)) {**\&#xA;**&#x20;           savedLanguage ?: LANGUAGES[0]**\&#xA;**&#x20;       } else {**\&#xA;**&#x20;           LANGUAGES[0]**\&#xA;**&#x20;       }**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Notification settings**\&#xA;**&#x20;   fun isNotificationsEnabled(context: Context): Boolean =**\&#xA;**&#x20;       prefs(context).getBoolean(KEY_NOTIF_ENABLED, true)**\
\&#xA;**&#x20;   fun isMedicationRemindersEnabled(context: Context): Boolean =**\&#xA;**&#x20;       prefs(context).getBoolean(KEY_MED_REMINDER_ENABLED, true)**\
\&#xA;**&#x20;   fun isAppointmentRemindersEnabled(context: Context): Boolean =**\&#xA;**&#x20;       prefs(context).getBoolean(KEY_APP_REMINDER_ENABLED, true)**\
\&#xA;**&#x20;   // Appearance settings**\&#xA;**&#x20;   fun isDarkModeEnabled(context: Context): Boolean =**\&#xA;**&#x20;       prefs(context).getBoolean(KEY_DARK_MODE, false)**\
\&#xA;**&#x20;   fun getTextSize(context: Context): String =**\&#xA;**&#x20;       prefs(context).getString(KEY_TEXT_SIZE, "Medium") ?: "Medium"**\
\&#xA;**&#x20;   // Display name**\&#xA;**&#x20;   fun displayName(context: Context): String {**\&#xA;**&#x20;       return InputValidator.displayName(getName(context), getEmail(context))**\&#xA;**&#x20;   }**\
\&#xA;**&#x20;   // Apply selected application language**\&#xA;**&#x20;   fun applyLocale(context: Context): Context {**\
\&#xA;**&#x20;       val languageTag = InputValidator.languageTag(getLanguage(context))**\
\&#xA;**&#x20;       val locale = Locale.forLanguageTag(languageTag)**\
\&#xA;**&#x20;       Locale.setDefault(locale)**\
\&#xA;**&#x20;       val config = Configuration(context.resources.configuration)**\&#xA;**&#x20;       config.setLocale(locale)**\
\&#xA;**&#x20;       return context.createConfigurationContext(config)**\&#xA;**&#x20;   }**\
**}**

