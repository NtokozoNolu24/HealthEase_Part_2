
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class AppointmentReminderActivity : AppCompatActivity() {

    private lateinit var etAppointmentName: EditText
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var btnSaveAppointment: Button
    private lateinit var btnBackAppointment: Button
    private lateinit var appointmentsContainer: LinearLayout

    private val appointmentRepository = AppointmentRepository()
    private var appointmentsListener: ValueEventListener? = null

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

        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        btnSelectTime.setOnClickListener {
            showTimePicker()
        }

        btnSaveAppointment.setOnClickListener {
            saveAppointment()
        }

        btnBackAppointment.setOnClickListener {
            finish()
        }

        loadAppointments()
    }

    private fun loadAppointments() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                this,
                "Please sign in to view your appointments.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        appointmentsListener = appointmentRepository.getAppointments(
            onDataChanged = { appointments ->

                appointmentsContainer.removeAllViews()

                appointments.forEach { appointment ->
                    addAppointmentToScreen(
                        appointment.id,
                        appointment.name,
                        appointment.date,
                        appointment.time
                    )
                }
            },
            onError = { message ->
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->

                selectedYear = year
                selectedMonth = month
                selectedDay = day

                btnSelectDate.text = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    day,
                    month + 1,
                    year
                )
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val timePicker = TimePickerDialog(
            this,
            { _, hour, minute ->

                selectedHour = hour
                selectedMinute = minute

                btnSelectTime.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hour,
                    minute
                )
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
            etAppointmentName.error =
                getString(R.string.please_enter_name)
            etAppointmentName.requestFocus()
            return
        }

        if (
            selectedYear == -1 ||
            selectedMonth == -1 ||
            selectedDay == -1 ||
            selectedHour == -1 ||
            selectedMinute == -1
        ) {
            Toast.makeText(
                this,
                R.string.please_select_date_time,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedCalendar = Calendar.getInstance().apply {
            set(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                0
            )
            set(Calendar.MILLISECOND, 0)
        }

        if (selectedCalendar.timeInMillis < System.currentTimeMillis()) {
            Toast.makeText(
                this,
                "Please select a future date and time.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val date = String.format(
            Locale.getDefault(),
            "%02d/%02d/%04d",
            selectedDay,
            selectedMonth + 1,
            selectedYear
        )

        val time = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            selectedHour,
            selectedMinute
        )

        val appointment = AppointmentReminder(
            name = name,
            date = date,
            time = time
        )

        btnSaveAppointment.isEnabled = false

        appointmentRepository.saveAppointment(
            appointment = appointment,

            onSuccess = {
                btnSaveAppointment.isEnabled = true

                Toast.makeText(
                    this,
                    R.string.appointment_saved,
                    Toast.LENGTH_SHORT
                ).show()

                clearAppointmentForm()
            },

            onError = { message ->
                btnSaveAppointment.isEnabled = true

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun clearAppointmentForm() {
        etAppointmentName.text.clear()

        btnSelectDate.text = getString(R.string.select_date)
        btnSelectTime.text = getString(R.string.select_time)

        selectedYear = -1
        selectedMonth = -1
        selectedDay = -1
        selectedHour = -1
        selectedMinute = -1
    }

    private fun addAppointmentToScreen(
        appointmentId: String,
        name: String,
        date: String,
        time: String
    ) {
        val appointmentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val appointmentText = TextView(this).apply {
            text = getString(
                R.string.reminder_format,
                name,
                date,
                time
            )
            textSize = 17f
        }

        val deleteButton = Button(this).apply {
            text = getString(R.string.delete)
        }

        deleteButton.setOnClickListener {
            deleteButton.isEnabled = false

            appointmentRepository.removeAppointment(
                appointmentId = appointmentId,

                onSuccess = {
                    Toast.makeText(
                        this@AppointmentReminderActivity,
                        "Appointment deleted.",
                        Toast.LENGTH_SHORT
                    ).show()
                },

                onError = { message ->
                    deleteButton.isEnabled = true

                    Toast.makeText(
                        this@AppointmentReminderActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        appointmentLayout.addView(appointmentText)
        appointmentLayout.addView(deleteButton)

        appointmentsContainer.addView(appointmentLayout)
    }

    override fun onDestroy() {
        super.onDestroy()

        val listener = appointmentsListener

        if (listener != null) {
            appointmentRepository.removeAppointmentsListener(listener)
            appointmentsListener = null
        }
    }
}