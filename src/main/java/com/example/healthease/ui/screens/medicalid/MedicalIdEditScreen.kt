package com.example.healthease.ui.screens.medicalid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.healthease.data.local.ProfileEntity
import com.example.healthease.ui.components.HealthEaseButton
import com.example.healthease.ui.components.HealthEaseTextField
import com.example.healthease.ui.theme.DangerRed
import com.example.healthease.ui.theme.PrimaryGreen
import com.example.healthease.ui.theme.SecondaryYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalIdEditScreen(
    onDone: () -> Unit,
    viewModel: MedicalIdViewModel = viewModel(
        factory = MedicalIdViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val app = LocalContext.current.applicationContext as HealthEaseApplication
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Local form state
    var bloodType by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var chronicConditions by remember { mutableStateOf("") }
    var currentMedications by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var emergencyRelation by remember { mutableStateOf("") }
    var physician by remember { mutableStateOf("") }
    var physicianPhone by remember { mutableStateOf("") }
    var insuranceProvider by remember { mutableStateOf("") }
    var insurancePolicy by remember { mutableStateOf("") }
    var insuranceExpiry by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    // Prefill when profile loads
    LaunchedEffect(state.profile) {
        if (!initialized && state.profile != null) {
            val p = state.profile!!
            bloodType = p.bloodType ?: ""
            allergies = p.allergies
            chronicConditions = p.chronicConditions
            currentMedications = p.currentMedications
            emergencyName = p.emergencyContactName ?: ""
            emergencyPhone = p.emergencyContactPhone ?: ""
            emergencyRelation = p.emergencyContactRelationship ?: ""
            physician = p.primaryPhysician ?: ""
            physicianPhone = p.physicianPhone ?: ""
            insuranceProvider = p.insuranceProvider ?: ""
            insurancePolicy = p.insurancePolicyNumber ?: ""
            insuranceExpiry = p.insuranceExpiry ?: ""
            initialized = true
        }
    }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            viewModel.clearMessage()
            onDone()
        }
    }

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var bloodMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Medical ID ✏️", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DangerRed)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(DangerRed.copy(alpha = 0.05f), SecondaryYellow.copy(alpha = 0.08f))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Blood Type dropdown
            ExposedDropdownMenuBox(
                expanded = bloodMenuExpanded,
                onExpandedChange = { bloodMenuExpanded = !bloodMenuExpanded }
            ) {
                OutlinedTextField(
                    value = bloodType.ifBlank { "Select blood type" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("🩸 Blood Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodMenuExpanded) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = bloodMenuExpanded,
                    onDismissRequest = { bloodMenuExpanded = false }
                ) {
                    bloodTypes.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t) },
                            onClick = { bloodType = t; bloodMenuExpanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = allergies, onValueChange = { allergies = it },
                label = "Allergies (comma separated)", leadingEmoji = "⚠️"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = chronicConditions, onValueChange = { chronicConditions = it },
                label = "Chronic conditions", leadingEmoji = "💊"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = currentMedications, onValueChange = { currentMedications = it },
                label = "Current medications", leadingEmoji = "💉"
            )
            Spacer(Modifier.height(20.dp))

            Text("🚨 Emergency Contact", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DangerRed)
            Spacer(Modifier.height(8.dp))

            HealthEaseTextField(
                value = emergencyName, onValueChange = { emergencyName = it },
                label = "Name", leadingEmoji = "👤"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = emergencyPhone, onValueChange = { emergencyPhone = it },
                label = "Phone", leadingEmoji = "📱",
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = emergencyRelation, onValueChange = { emergencyRelation = it },
                label = "Relationship", leadingEmoji = "❤️"
            )
            Spacer(Modifier.height(20.dp))

            Text("👨‍⚕️ Primary Physician", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
            Spacer(Modifier.height(8.dp))

            HealthEaseTextField(
                value = physician, onValueChange = { physician = it },
                label = "Doctor name", leadingEmoji = "👨‍⚕️"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = physicianPhone, onValueChange = { physicianPhone = it },
                label = "Doctor phone", leadingEmoji = "📞",
                keyboardType = KeyboardType.Phone
            )
            Spacer(Modifier.height(20.dp))

            Text("🏥 Health Insurance (optional)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
            Spacer(Modifier.height(8.dp))

            HealthEaseTextField(
                value = insuranceProvider, onValueChange = { insuranceProvider = it },
                label = "Provider", leadingEmoji = "🏥"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = insurancePolicy, onValueChange = { insurancePolicy = it },
                label = "Policy number", leadingEmoji = "🔢"
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = insuranceExpiry, onValueChange = { insuranceExpiry = it },
                label = "Expiry (yyyy-MM-dd)", leadingEmoji = "📅"
            )
            Spacer(Modifier.height(24.dp))

            HealthEaseButton(
                text = "Save Medical ID 💾",
                onClick = {
                    val uid = app.currentUserId ?: return@HealthEaseButton
                    val existing = state.profile
                    val entity = (existing ?: ProfileEntity(userId = uid)).copy(
                        userId = uid,
                        bloodType = bloodType.ifBlank { null },
                        allergies = allergies.trim(),
                        chronicConditions = chronicConditions.trim(),
                        currentMedications = currentMedications.trim(),
                        emergencyContactName = emergencyName.ifBlank { null },
                        emergencyContactPhone = emergencyPhone.ifBlank { null },
                        emergencyContactRelationship = emergencyRelation.ifBlank { null },
                        primaryPhysician = physician.ifBlank { null },
                        physicianPhone = physicianPhone.ifBlank { null },
                        insuranceProvider = insuranceProvider.ifBlank { null },
                        insurancePolicyNumber = insurancePolicy.ifBlank { null },
                        insuranceExpiry = insuranceExpiry.ifBlank { null },
                        updatedAt = System.currentTimeMillis()
                    )
                    viewModel.save(entity)
                }
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}