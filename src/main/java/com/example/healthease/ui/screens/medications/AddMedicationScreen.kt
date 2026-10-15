package com.example.healthease.ui.screens.medications

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
import com.example.healthease.data.local.MedicationEntity
import com.example.healthease.ui.components.HealthEaseButton
import com.example.healthease.ui.components.HealthEaseTextField
import com.example.healthease.ui.theme.PrimaryGreen
import com.example.healthease.ui.theme.SecondaryYellow
import com.example.healthease.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    onDone: () -> Unit,
    viewModel: MedicationViewModel = viewModel(
        factory = MedicationViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val app = LocalContext.current.applicationContext as HealthEaseApplication
    val state by viewModel.state.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Once daily") }
    var times by remember { mutableStateOf("08:00") }
    var withFood by remember { mutableStateOf(false) }
    var instructions by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var dosageError by remember { mutableStateOf<String?>(null) }
    var freqMenu by remember { mutableStateOf(false) }

    val frequencies = listOf("Once daily", "Twice daily", "Three times daily", "Every other day", "Weekly", "As needed")

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            viewModel.clearMessage()
            onDone()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medication 💊", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(PrimaryGreen.copy(alpha = 0.05f), SecondaryYellow.copy(alpha = 0.08f))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            HealthEaseTextField(
                value = name, onValueChange = { name = it; nameError = null },
                label = "Medication name", leadingEmoji = "💊",
                isError = nameError != null, errorMessage = nameError
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = dosage, onValueChange = { dosage = it; dosageError = null },
                label = "Dosage (e.g. 500mg)", leadingEmoji = "⚖️",
                isError = dosageError != null, errorMessage = dosageError
            )
            Spacer(Modifier.height(14.dp))

            // Frequency dropdown
            ExposedDropdownMenuBox(
                expanded = freqMenu,
                onExpandedChange = { freqMenu = !freqMenu }
            ) {
                OutlinedTextField(
                    value = frequency, onValueChange = {}, readOnly = true,
                    label = { Text("Frequency") },
                    leadingIcon = { Text("🔁", fontSize = 20.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = freqMenu, onDismissRequest = { freqMenu = false }) {
                    frequencies.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { frequency = it; freqMenu = false })
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = times, onValueChange = { times = it },
                label = "Times (e.g. 08:00,20:00)", leadingEmoji = "⏰"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = instructions, onValueChange = { instructions = it },
                label = "Instructions (optional)", leadingEmoji = "📝"
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = withFood, onCheckedChange = { withFood = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryGreen)
                )
                Text("🍽️ Take with food", fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))

            HealthEaseButton(
                text = "Save Medication 💾",
                onClick = {
                    var ok = true
                    if (name.isBlank()) { nameError = "Required"; ok = false }
                    if (dosage.isBlank()) { dosageError = "Required"; ok = false }
                    if (!ok) return@HealthEaseButton
                    val uid = app.currentUserId ?: return@HealthEaseButton
                    viewModel.add(
                        MedicationEntity(
                            userId = uid,
                            name = name.trim(),
                            dosage = dosage.trim(),
                            frequency = frequency,
                            scheduleTimes = times.trim(),
                            startDate = DateTimeUtils.todayIso(),
                            withFood = withFood,
                            instructions = instructions.ifBlank { null }
                        )
                    )
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}