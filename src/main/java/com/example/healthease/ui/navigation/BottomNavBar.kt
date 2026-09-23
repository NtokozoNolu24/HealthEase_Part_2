package com.example.healthease.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.healthease.ui.theme.PrimaryGreen

data class BottomTab(val route: String, val emoji: String, val label: String)

val bottomTabs = listOf(
    BottomTab("dashboard",    "🏠", "Home"),
    BottomTab("medical_id",   "🩺", "Medical ID"),
    BottomTab("medications",  "💊", "Meds"),
    BottomTab("articles",     "📰", "Articles"),   // ← NEW
    BottomTab("settings",     "⚙️", "Settings")
)

@Composable
fun HealthEaseBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onTabSelected(tab) },
                icon = { Text(tab.emoji, fontSize = 20.sp) },
                label = { Text(tab.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryGreen,
                    selectedTextColor = PrimaryGreen,
                    indicatorColor = PrimaryGreen.copy(alpha = 0.15f)
                )
            )
        }
    }
}