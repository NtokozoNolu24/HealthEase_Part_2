
package com.example.healthease_part_2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var spLanguage: Spinner

    private lateinit var spTextSize: Spinner
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var switchMedication: SwitchCompat
    private lateinit var switchAppointments: SwitchCompat
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        spLanguage = findViewById(R.id.spLanguage)
        switchNotifications = findViewById(R.id.switchNotifications)
        switchMedication = findViewById(R.id.switchMedication)
        switchAppointments = findViewById(R.id.switchAppointments)
        switchDarkMode = findViewById(R.id.switchDarkMode)
        btnSave = findViewById(R.id.btnSaveSettings)
        btnBack = findViewById(R.id.btnBack)
        spTextSize = findViewById(R.id.spTextSize)

        setupLanguageSpinner()
        loadSettings()

        btnSave.setOnClickListener {
            saveSettings()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            UserSession.LANGUAGES
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spLanguage.adapter = adapter
    }

    private fun loadSettings() {

        val savedLanguage = UserSession.getLanguage(this)
        val languagePosition = UserSession.LANGUAGES.indexOf(savedLanguage)

        if (languagePosition >= 0) {
            spLanguage.setSelection(languagePosition)
        }

        switchNotifications.isChecked =
            UserSession.isNotificationsEnabled(this)

        switchMedication.isChecked =
            UserSession.isMedicationRemindersEnabled(this)

        switchAppointments.isChecked =
            UserSession.isAppointmentRemindersEnabled(this)

        switchDarkMode.isChecked =
            UserSession.isDarkModeEnabled(this)

        val textSizePosition = UserSession.TEXT_SIZES.indexOf(
            UserSession.getTextSize(this)
        )

        if (textSizePosition >= 0) {
            spTextSize.setSelection(textSizePosition)
        }
    }

    private fun saveSettings() {

        val selectedLanguage = spLanguage.selectedItem.toString()
        val oldLanguage = UserSession.getLanguage(this)

        val darkModeEnabled = switchDarkMode.isChecked

        UserSession.saveSettings(
            context = this,
            language = selectedLanguage,
            notifEnabled = switchNotifications.isChecked,
            medEnabled = switchMedication.isChecked,
            appEnabled = switchAppointments.isChecked,
            darkMode = switchDarkMode.isChecked,
            textSize = spTextSize.selectedItem.toString()


        )

        // Apply the selected appearance throughout the app
        AppCompatDelegate.setDefaultNightMode(
            if (darkModeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        Toast.makeText(
            this,
            getString(R.string.settings_saved),
            Toast.LENGTH_SHORT
        ).show()

        if (oldLanguage != selectedLanguage) {
            val intent = Intent(this, DashboardActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}
