package com.example.healthease.ui.screens.appointments

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
import com.example.healthease.data.local.AppointmentEntity
import com.example.healthease.ui.theme.*
import com.example.healthease.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    onAdd: () -> Unit,
    viewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                containerColor = AccentTeal,
                contentColor = Color.White
            ) { Text("➕ Add Appointment", fontWeight = FontWeight.SemiBold) }
        },
        topBar = {
            TopAppBar(
                title = { Text("📅 Appointments", fontWeight = FontWeight.Bold, color = Color.White) },
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
        ) {
            if (state.all.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No appointments yet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tap ➕ to add your first appointment.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.upcoming.isNotEmpty()) {
                        item {
                            Text(
                                "🔜 Upcoming",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = AccentTeal,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(state.upcoming, key = { it.appointmentId }) { a ->
                            AppointmentCard(
                                a = a,
                                onAttend = { viewModel.updateStatus(a.appointmentId, "attended") },
                                onMiss = { viewModel.updateStatus(a.appointmentId, "missed") },
                                onDelete = { viewModel.delete(a.appointmentId) }
                            )
                        }
                    }
                    val past = state.all.filter { it.appointmentDate < System.currentTimeMillis() }
                    if (past.isNotEmpty()) {
                        item {
                            Text(
                                "📜 Past",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(past, key = { it.appointmentId }) { a ->
                            AppointmentCard(
                                a = a,
                                onAttend = { viewModel.updateStatus(a.appointmentId, "attended") },
                                onMiss = { viewModel.updateStatus(a.appointmentId, "missed") },
                                onDelete = { viewModel.delete(a.appointmentId) }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    a: AppointmentEntity,
    onAttend: () -> Unit,
    onMiss: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (a.status) {
        "attended" -> PrimaryGreen
        "missed" -> DangerRed
        "cancelled" -> TextSecondary
        else -> AccentTeal
    }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📅", fontSize = 28.sp)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(a.doctorName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (!a.specialty.isNullOrBlank())
                        Text(a.specialty, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AssistChip(
                    onClick = {},
                    label = { Text(a.status, fontSize = 11.sp, color = statusColor) }
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("🕐 ${DateTimeUtils.formatDateTime(a.appointmentDate)}", fontSize = 13.sp)
            if (!a.clinicName.isNullOrBlank())
                Text("🏥 ${a.clinicName}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!a.address.isNullOrBlank())
                Text("📍 ${a.address}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!a.notes.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("📝 ${a.notes}", fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onAttend,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("✅ Attended", fontSize = 12.sp) }
                OutlinedButton(
                    onClick = onMiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) { Text("❌ Missed", fontSize = 12.sp) }
                IconButton(onClick = onDelete) { Text("🗑️", fontSize = 18.sp) }
            }
        }
    }
}