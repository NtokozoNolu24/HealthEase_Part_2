package com.example.healthease.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthease.HealthEaseApplication
import com.example.healthease.ui.screens.appointments.AddAppointmentScreen
import com.example.healthease.ui.screens.appointments.AppointmentsScreen
import com.example.healthease.ui.screens.articles.ArticleDetailScreen
import com.example.healthease.ui.screens.articles.ArticlesFeedScreen
import com.example.healthease.ui.screens.articles.BookmarksScreen
import com.example.healthease.ui.screens.dashboard.DashboardScreen
import com.example.healthease.ui.screens.login.LoginScreen
import com.example.healthease.ui.screens.medicalid.MedicalIdEditScreen
import com.example.healthease.ui.screens.medicalid.MedicalIdScreen
import com.example.healthease.ui.screens.medications.AddMedicationScreen
import com.example.healthease.ui.screens.medications.MedicationsScreen
import com.example.healthease.ui.screens.notifications.NotificationCenterScreen
import com.example.healthease.ui.screens.register.RegisterScreen
import com.example.healthease.ui.screens.settings.SettingsScreen
import com.example.healthease.ui.screens.splash.SplashScreen

object Routes {
    // Auth
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main tabs
    const val DASHBOARD = "dashboard"
    const val MEDICAL_ID = "medical_id"
    const val MEDICATIONS = "medications"
    const val ARTICLES = "articles"
    const val SETTINGS = "settings"

    // Sub-screens
    const val MEDICAL_ID_EDIT = "medical_id_edit"
    const val ADD_MEDICATION = "add_medication"
    const val APPOINTMENTS = "appointments"
    const val ADD_APPOINTMENT = "add_appointment"
    const val ARTICLE_DETAIL = "article_detail"
    const val BOOKMARKS = "bookmarks"
    const val NOTIFICATIONS = "notifications"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val app = LocalContext.current.applicationContext as HealthEaseApplication
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Show bottom bar only on top-level tab routes
    val showBottomBar = currentRoute in bottomTabs.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                HealthEaseBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = if (showBottomBar) Modifier.padding(padding) else Modifier
        ) {

            // ---------- SPLASH ----------
            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // ---------- AUTH ----------
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onLoginSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // ---------- DASHBOARD ----------
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onLogout = {
                        app.clearCurrentUser()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    },
                    onNavigateToMedicalId = { navController.navigate(Routes.MEDICAL_ID) },
                    onNavigateToMedications = { navController.navigate(Routes.MEDICATIONS) },
                    onNavigateToAppointments = { navController.navigate(Routes.APPOINTMENTS) },
                    onNavigateToArticles = { navController.navigate(Routes.ARTICLES) },
                    onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                )
            }

            // ---------- MEDICAL ID ----------
            composable(Routes.MEDICAL_ID) {
                MedicalIdScreen(
                    onEdit = { navController.navigate(Routes.MEDICAL_ID_EDIT) }
                )
            }
            composable(Routes.MEDICAL_ID_EDIT) {
                MedicalIdEditScreen(
                    onDone = { navController.popBackStack() }
                )
            }

            // ---------- MEDICATIONS ----------
            composable(Routes.MEDICATIONS) {
                MedicationsScreen(
                    onAdd = { navController.navigate(Routes.ADD_MEDICATION) }
                )
            }
            composable(Routes.ADD_MEDICATION) {
                AddMedicationScreen(
                    onDone = { navController.popBackStack() }
                )
            }

            // ---------- APPOINTMENTS ----------
            composable(Routes.APPOINTMENTS) {
                AppointmentsScreen(
                    onAdd = { navController.navigate(Routes.ADD_APPOINTMENT) }
                )
            }
            composable(Routes.ADD_APPOINTMENT) {
                AddAppointmentScreen(
                    onDone = { navController.popBackStack() }
                )
            }

            // ---------- ARTICLES ----------
            composable(Routes.ARTICLES) {
                ArticlesFeedScreen(
                    onOpenArticle = { id ->
                        navController.navigate("${Routes.ARTICLE_DETAIL}/$id")
                    },
                    onOpenBookmarks = { navController.navigate(Routes.BOOKMARKS) }
                )
            }
            composable("${Routes.ARTICLE_DETAIL}/{articleId}") { backStack ->
                val id = backStack.arguments?.getString("articleId") ?: return@composable
                ArticleDetailScreen(
                    articleId = id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.BOOKMARKS) {
                BookmarksScreen(
                    onBack = { navController.popBackStack() },
                    onOpenArticle = { id ->
                        navController.navigate("${Routes.ARTICLE_DETAIL}/$id")
                    }
                )
            }

            // ---------- SETTINGS ----------
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onLogout = {
                        app.clearCurrentUser()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    },
                    onDeleteAccount = {
                        app.clearCurrentUser()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }

            // ---------- NOTIFICATIONS ----------
            composable(Routes.NOTIFICATIONS) {
                NotificationCenterScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}