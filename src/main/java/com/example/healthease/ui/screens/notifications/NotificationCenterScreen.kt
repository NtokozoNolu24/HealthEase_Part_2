package com.example.healthease.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.healthease.data.local.NotificationEntity
import com.example.healthease.ui.theme.*
import com.example.healthease.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(
    onBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔 Notifications", fontWeight = FontWeight.Bold, color = Color.White)
                        if (state.unreadCount > 0) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                color = DangerRed,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "${state.unreadCount}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Text("⋮", color = Color.White, fontSize = 22.sp)
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("✅ Mark all as read") },
                                onClick = { viewModel.markAllRead(); menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("🗑️ Clear all") },
                                onClick = { viewModel.clearAll(); menuExpanded = false }
                            )
                        }
                    }
                },
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
            if (state.notifications.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔕", fontSize = 64.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No notifications yet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "You'll see reminders, tips, and system alerts here.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.notifications, key = { it.notificationId }) { n ->
                        NotificationCard(n) { viewModel.markRead(n.notificationId) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(n: NotificationEntity, onClick: () -> Unit) {
    val bgColor = if (n.isRead) Color.White else PrimaryGreen.copy(alpha = 0.10f)
    val emoji = when (n.type) {
        "reminder" -> "⏰"
        "tip" -> "💡"
        "system" -> "⚙️"
        "sync" -> "🔄"
        else -> "🔔"
    }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        n.title,
                        fontWeight = if (n.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (!n.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(PrimaryGreen, shape = RoundedCornerShape(50))
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    n.body,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    DateTimeUtils.formatDateTime(n.createdAt),
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}