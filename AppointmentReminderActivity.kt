package com.example.healthease_part_2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AppointmentReminderActivity : AppCompatActivity() {

    private lateinit var etAppointmentName: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var btnSaveAppointment: Button
    private lateinit var btnBackAppointment: Button
    private lateinit var appointmentsContainer: LinearLayout

    private var selectedYear = -1
    private var selectedMonth = -1
    private var selectedDay = -1
    private var selectedHour = -1
    private var selectedMinute = -1

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(UserSession.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_reminder)

        etAppointmentName = findViewById(R.id.etAppointmentName)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectTime = findViewById(R.id.btnSelectTime)
        btnSaveAppointment = findViewById(R.id.btnSaveAppointment)
        btnBackAppointment = findViewById(R.id.btnBackAppointment)
        appointmentsContainer = findViewById(R.id.appointmentsContainer)

        btnSelectDate.setOnClickListener { showDatePicker() }
        btnSelectTime.setOnClickListener { showTimePicker() }
        btnSaveAppointment.setOnClickListener { saveAppointment() }
        btnBackAppointment.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day
                btnSelectDate.text = "$day/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
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

    private fun saveAppointment() {
        val name = etAppointmentName.text.toString().trim()
        if (name.isEmpty()) {
            etAppointmentName.error = getString(R.string.please_enter_name)
            return
        }
        if (selectedYear == -1 || selectedHour == -1) {
            Toast.makeText(this, R.string.please_select_date_time, Toast.LENGTH_SHORT).show()
            return
        }

        val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        val time = String.format("%02d:%02d", selectedHour, selectedMinute)
        
        addAppointmentToScreen(name, date, time)
        
        Toast.makeText(this, R.string.appointment_saved, Toast.LENGTH_SHORT).show()
        
        // Clear inputs
        etAppointmentName.text.clear()
        btnSelectDate.text = getString(R.string.select_date)
        btnSelectTime.text = getString(R.string.select_time)
        selectedYear = -1
        selectedHour = -1
    }

    private fun addAppointmentToScreen(name: String, date: String, time: String) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)

        val text = TextView(this)
        text.text = getString(R.string.reminder_format, name, date, time)
        text.textSize = 17f

        val deleteBtn = Button(this)
        deleteBtn.text = getString(R.string.delete)
        deleteBtn.setOnClickListener { appointmentsContainer.removeView(layout) }

        layout.addView(text)
        layout.addView(deleteBtn)
        appointmentsContainer.addView(layout)
    }
}
