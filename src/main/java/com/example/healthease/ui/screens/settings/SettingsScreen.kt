package com.example.healthease.ui.screens.settings

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
import com.example.healthease.data.local.UserSettingsEntity
import com.example.healthease.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state.settings

    // Snackbar for save messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            snackbarHostState.showSnackbar(state.savedMessage!!)
            viewModel.clearMessage()
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Settings", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)
            )
        }
    ) { padding ->
        if (state.isLoading || s == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
            return@Scaffold
        }

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
            SectionCard(title = "🌍 General") {
                // Language
                DropdownRow(
                    label = "Language",
                    value = when (s.language) {
                        "zu" -> "isiZulu"; "af" -> "Afrikaans"; else -> "English"
                    },
                    options = listOf("English", "isiZulu", "Afrikaans"),
                    onSelect = { display ->
                        val code = when (display) {
                            "isiZulu" -> "zu"; "Afrikaans" -> "af"; else -> "en"
                        }
                        viewModel.update { it.copy(language = code) }
                    }
                )
                Divider(Modifier.padding(vertical = 8.dp))
                // Theme
                DropdownRow(
                    label = "Theme",
                    value = s.theme.replaceFirstChar { it.uppercase() },
                    options = listOf("Light", "Dark", "System"),
                    onSelect = { v -> viewModel.update { it.copy(theme = v.lowercase()) } }
                )
                Divider(Modifier.padding(vertical = 8.dp))
                // Font size
                DropdownRow(
                    label = "Font size",
                    value = s.fontSize.replaceFirstChar { it.uppercase() },
                    options = listOf("Small", "Medium", "Large"),
                    onSelect = { v -> viewModel.update { it.copy(fontSize = v.lowercase()) } }
                )
                Divider(Modifier.padding(vertical = 8.dp))
                // Units
                DropdownRow(
                    label = "Units",
                    value = s.units.replaceFirstChar { it.uppercase() },
                    options = listOf("Metric", "Imperial"),
                    onSelect = { v -> viewModel.update { it.copy(units = v.lowercase()) } }
                )
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "🔔 Notifications") {
                ToggleRow("Reminder alerts", s.notifyReminders) {
                    viewModel.update { st -> st.copy(notifyReminders = it) }
                }
                ToggleRow("Health tips", s.notifyTips) {
                    viewModel.update { st -> st.copy(notifyTips = it) }
                }
                ToggleRow("System alerts", s.notifySystem) {
                    viewModel.update { st -> st.copy(notifySystem = it) }
                }
                ToggleRow("Sync status", s.notifySync) {
                    viewModel.update { st -> st.copy(notifySync = it) }
                }
                Divider(Modifier.padding(vertical = 8.dp))
                ToggleRow("Quiet hours (22:00–06:00)", s.quietHoursEnabled) {
                    viewModel.update { st -> st.copy(quietHoursEnabled = it) }
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "🔒 Privacy") {
                ToggleRow("Data sharing (anonymous)", s.dataSharing) {
                    viewModel.update { st -> st.copy(dataSharing = it) }
                }
                ToggleRow("Biometric unlock", s.biometricUnlock) {
                    viewModel.update { st -> st.copy(biometricUnlock = it) }
                }
                ToggleRow("Show Medical ID on lock screen", s.lockScreenMedicalId) {
                    viewModel.update { st -> st.copy(lockScreenMedicalId = it) }
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "💾 Data") {
                OutlinedButton(
                    onClick = { viewModel.update { it } }, // placeholder
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("📤 Export data (JSON) — coming soon") }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Storage: local SQLite only • Nothing sent to any server ✅",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            SectionCard(title = "🚨 Account") {
                Button(
                    onClick = onLogout,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🚪 Log out", fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🗑️ Delete account") }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete account?") },
            text = { Text("This will permanently remove all your data from this device. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDeleteAccount()
                }) { Text("Delete", color = DangerRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryGreen)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = PrimaryGreen)
        )
    }
}

@Composable
private fun DropdownRow(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Box {
            TextButton(onClick = { expanded = true }) {
                Text("$value ▾", color = PrimaryGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = { onSelect(opt); expanded = false }
                    )
                }
            }
        }
    }
}