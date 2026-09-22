package com.example.healthease_part_2

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MedicationReminderActivity : AppCompatActivity() {

    private lateinit var etMedicationName: EditText
    private lateinit var btnSelectTime: Button
    private lateinit var spRepeat: Spinner
    private lateinit var btnSaveMedication: Button
    private lateinit var btnBackMedication: Button
    private lateinit var remindersContainer: LinearLayout

    private var selectedHour = -1
    private var selectedMinute = -1

    private fun getRepeatOptions() = arrayOf(
        getString(R.string.once),
        getString(R.string.daily),
        getString(R.string.weekly)
    )

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_reminder)

        etMedicationName = findViewById(R.id.etMedicationName)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        spRepeat = findViewById(R.id.spRepeat)
        btnSaveMedication = findViewById(R.id.btnSaveMedication)
        btnBackMedication = findViewById(R.id.btnBackMedication)
        remindersContainer = findViewById(R.id.remindersContainer)

        setupRepeatSpinner()

        btnSelectTime.setOnClickListener { showTimePicker() }
        btnSaveMedication.setOnClickListener { saveMedicationReminder() }
        btnBackMedication.setOnClickListener { finish() }
    }

    private fun setupRepeatSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            getRepeatOptions()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRepeat.adapter = adapter
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            this,
            { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                btnSelectTime.text = String.format("%02d:%02d", hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun saveMedicationReminder() {
        val medicationName = etMedicationName.text.toString().trim()
        if (medicationName.isEmpty()) {
            etMedicationName.error = getString(R.string.please_enter_medication)
            return
        }
        if (selectedHour == -1) {
            Toast.makeText(this, R.string.please_select_time, Toast.LENGTH_SHORT).show()
            return
        }

        val repeat = spRepeat.selectedItem.toString()
        val time = String.format("%02d:%02d", selectedHour, selectedMinute)

        addReminderToScreen(medicationName, time, repeat)

        Toast.makeText(this, R.string.medication_saved, Toast.LENGTH_SHORT).show()

        etMedicationName.text.clear()
        btnSelectTime.text = getString(R.string.select_time)
        selectedHour = -1
    }

    private fun addReminderToScreen(name: String, time: String, repeat: String) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)

        val text = TextView(this)
        text.text = getString(R.string.reminder_format, name, repeat, time)
        text.textSize = 17f

        val deleteBtn = Button(this)
        deleteBtn.text = getString(R.string.delete)
        deleteBtn.setOnClickListener { remindersContainer.removeView(layout) }

        layout.addView(text)
        layout.addView(deleteBtn)
        remindersContainer.addView(layout)
    }
}
