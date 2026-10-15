package com.example.healthease.ui.screens.medications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.healthease.data.local.MedicationEntity
import com.example.healthease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationsScreen(
    onAdd: () -> Unit,
    viewModel: MedicationViewModel = viewModel(
        factory = MedicationViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) { Text("➕ Add Medication", fontWeight = FontWeight.SemiBold) }
        },
        topBar = {
            TopAppBar(
                title = { Text("💊 My Medications", fontWeight = FontWeight.Bold, color = Color.White) },
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
        ) {
            // Adherence banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📊", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("30-day Adherence", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${state.adherencePercent()}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }
            }

            if (state.medications.isEmpty()) {
                Box(
                    Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💊", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No medications yet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tap ➕ to add your first medication and start tracking adherence.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.medications, key = { it.medicationId }) { med ->
                        MedicationCard(
                            med = med,
                            onTake = { viewModel.logDose(med, true) },
                            onSkip = { viewModel.logDose(med, false, "user skipped") },
                            onDelete = { viewModel.delete(med.medicationId) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(
    med: MedicationEntity,
    onTake: () -> Unit,
    onSkip: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💊", fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(med.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${med.dosage} • ${med.frequency}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) { Text("🗑️", fontSize = 20.sp) }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏰ ${med.scheduleTimes}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (med.withFood) {
                    Spacer(Modifier.width(8.dp))
                    Text("🍽️ with food", fontSize = 12.sp, color = WarningOrange)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onTake,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.weight(1f)
                ) { Text("✅ Taken", fontSize = 13.sp) }
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("⏭️ Skip", fontSize = 13.sp) }
            }
        }
    }
}