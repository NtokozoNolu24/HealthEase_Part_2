package com.example.healthease.ui.screens.medicalid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthease.HealthEaseApplication
import com.example.healthease.ui.theme.DangerRed
import com.example.healthease.ui.theme.PrimaryGreen
import com.example.healthease.ui.theme.PrimaryGreenLight
import com.example.healthease.ui.theme.SecondaryYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalIdScreen(
    onEdit: () -> Unit,
    viewModel: MedicalIdViewModel = viewModel(
        factory = MedicalIdViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val profile = state.profile

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🩺 Medical ID",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    TextButton(onClick = onEdit) {
                        Text("Edit ✏️", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                },
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
                        listOf(
                            DangerRed.copy(alpha = 0.06f),
                            SecondaryYellow.copy(alpha = 0.08f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                "⚠️ Emergency information — accessible without login",
                fontSize = 12.sp,
                color = DangerRed,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
                return@Column
            }

            if (profile == null) {
                EmptyMedicalIdCard(onEdit = onEdit)
                return@Column
            }

            // Blood Type — Big card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DangerRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🩸 Blood Type", color = Color.White, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = profile.bloodType?.ifBlank { "—" } ?: "—",
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            InfoSection(
                title = "🚨 Emergency Contact",
                items = listOf(
                    "Name" to (profile.emergencyContactName ?: "—"),
                    "Phone" to (profile.emergencyContactPhone ?: "—"),
                    "Relationship" to (profile.emergencyContactRelationship ?: "—")
                )
            )
            Spacer(Modifier.height(12.dp))

            InfoSection(
                title = "⚠️ Allergies",
                items = listOf("Allergies" to (profile.allergies.ifBlank { "None listed" }))
            )
            Spacer(Modifier.height(12.dp))

            InfoSection(
                title = "💊 Chronic Conditions",
                items = listOf("Conditions" to (profile.chronicConditions.ifBlank { "None listed" }))
            )
            Spacer(Modifier.height(12.dp))

            InfoSection(
                title = "💉 Current Medications",
                items = listOf("Medications" to (profile.currentMedications.ifBlank { "None listed" }))
            )
            Spacer(Modifier.height(12.dp))

            InfoSection(
                title = "👨‍⚕️ Primary Physician",
                items = listOf(
                    "Name" to (profile.primaryPhysician ?: "—"),
                    "Phone" to (profile.physicianPhone ?: "—")
                )
            )
            Spacer(Modifier.height(12.dp))

            if (!profile.insuranceProvider.isNullOrBlank()) {
                InfoSection(
                    title = "🏥 Health Insurance",
                    items = listOf(
                        "Provider" to (profile.insuranceProvider ?: "—"),
                        "Policy #" to (profile.insurancePolicyNumber ?: "—"),
                        "Expires" to (profile.insuranceExpiry ?: "—")
                    )
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "⚠️ In a real emergency, always call 10177 (ambulance) or 112 (mobile).",
                fontSize = 12.sp,
                color = DangerRed,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, items: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryGreen)
            Spacer(Modifier.height(8.dp))
            items.forEach { (k, v) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(k, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(v, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun EmptyMedicalIdCard(onEdit: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🩺", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "No Medical ID yet",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Add your blood type, allergies, conditions, and emergency contacts so they're available when you need them most.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onEdit,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Add Medical Info ✨", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}