package com.example.healthease.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthease.HealthEaseApplication
import com.example.healthease.ui.components.*
import com.example.healthease.ui.theme.PrimaryGreen
import com.example.healthease.ui.theme.SecondaryYellow

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.registerSuccess) {
        if (state.registerSuccess) onRegisterSuccess(state.userName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        SecondaryYellow.copy(alpha = 0.12f),
                        PrimaryGreen.copy(alpha = 0.10f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            HealthEaseLogo()
            Spacer(Modifier.height(28.dp))

            Text(
                text = "Create Account ✨",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Join HealthEase and take control 💚",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            if (state.errorMessage != null) {
                ErrorBanner(message = state.errorMessage!!)
                Spacer(Modifier.height(16.dp))
            }

            HealthEaseTextField(
                value = state.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = "Full Name",
                leadingEmoji = "👤",
                isError = state.fullNameError != null,
                errorMessage = state.fullNameError
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = "Email",
                leadingEmoji = "📧",
                keyboardType = KeyboardType.Email,
                isError = state.emailError != null,
                errorMessage = state.emailError
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                label = "Phone (optional)",
                leadingEmoji = "📱",
                keyboardType = KeyboardType.Phone,
                isError = state.phoneError != null,
                errorMessage = state.phoneError
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                leadingEmoji = "🔒",
                isPassword = true,
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )
            Spacer(Modifier.height(14.dp))

            HealthEaseTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirm Password",
                leadingEmoji = "🔐",
                isPassword = true,
                isError = state.confirmPasswordError != null,
                errorMessage = state.confirmPasswordError
            )

            Spacer(Modifier.height(24.dp))
            HealthEaseButton(
                text = "Create Account 🎉",
                onClick = viewModel::register,
                loading = state.isLoading
            )

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Already have an account? ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        "Sign In 🔓",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}