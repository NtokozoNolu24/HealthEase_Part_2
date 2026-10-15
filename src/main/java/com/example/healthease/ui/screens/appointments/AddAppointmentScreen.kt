package com.example.healthease.ui.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.AppointmentEntity
import com.example.healthease.ui.components.HealthEaseButton
import com.example.healthease.ui.components.HealthEaseTextField
import com.example.healthease.ui.theme.AccentTeal
import com.example.healthease.ui.theme.SecondaryYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    onDone: () -> Unit,
    viewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val app = LocalContext.current.applicationContext as HealthEaseApplication
    val state by viewModel.state.collectAsStateWithLifecycle()

    var doctor by remember { mutableStateOf("") }
    var clinic by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }   // yyyy-MM-dd HH:mm
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var prep by remember { mutableStateOf("") }
    var doctorErr by remember { mutableStateOf<String?>(null) }
    var dateErr by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) { viewModel.clearMessage(); onDone() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Appointment 📅", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AccentTeal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(AccentTeal.copy(alpha = 0.05f), SecondaryYellow.copy(alpha = 0.08f))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            HealthEaseTextField(
                value = doctor, onValueChange = { doctor = it; doctorErr = null },
                label = "Doctor / Clinic name", leadingEmoji = "👨‍⚕️",
                isError = doctorErr != null, errorMessage = doctorErr
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = clinic, onValueChange = { clinic = it },
                label = "Clinic / Hospital (optional)", leadingEmoji = "🏥"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = specialty, onValueChange = { specialty = it },
                label = "Specialty (e.g. Cardiologist)", leadingEmoji = "🩺"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = dateText, onValueChange = { dateText = it; dateErr = null },
                label = "Date & Time (yyyy-MM-dd HH:mm)", leadingEmoji = "🕐",
                isError = dateErr != null, errorMessage = dateErr
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = address, onValueChange = { address = it },
                label = "Address / directions", leadingEmoji = "📍"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = prep, onValueChange = { prep = it },
                label = "Prep checklist (comma separated)", leadingEmoji = "📋"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = notes, onValueChange = { notes = it },
                label = "Notes", leadingEmoji = "📝"
            )
            Spacer(Modifier.height(24.dp))

            HealthEaseButton(
                text = "Save Appointment 💾",
                onClick = {
                    var ok = true
                    if (doctor.isBlank()) { doctorErr = "Required"; ok = false }
                    val millis = parseDateTime(dateText)
                    if (millis == null) { dateErr = "Use yyyy-MM-dd HH:mm"; ok = false }
                    if (!ok || millis == null) return@HealthEaseButton
                    val uid = app.currentUserId ?: return@HealthEaseButton
                    viewModel.add(
                        AppointmentEntity(
                            userId = uid,
                            doctorName = doctor.trim(),
                            clinicName = clinic.ifBlank { null },
                            specialty = specialty.ifBlank { null },
                            appointmentDate = millis,
                            address = address.ifBlank { null },
                            notes = notes.ifBlank { null },
                            prepChecklist = prep.trim()
                        )
                    )
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun parseDateTime(input: String): Long? {
    return try {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        fmt.isLenient = false
        fmt.parse(input.trim())?.time
    } catch (e: Exception) {
        null
    }
}