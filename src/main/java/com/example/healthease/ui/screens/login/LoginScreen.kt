package com.example.healthease.ui.screens.login

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
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.factory(
            LocalContext.current.applicationContext as HealthEaseApplication
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) onLoginSuccess(state.userName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PrimaryGreen.copy(alpha = 0.08f),
                        SecondaryYellow.copy(alpha = 0.10f)
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
            Spacer(Modifier.height(24.dp))
            HealthEaseLogo()
            Spacer(Modifier.height(36.dp))

            Text(
                text = "Welcome Back 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Sign in to continue your health journey",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(28.dp))

            if (state.errorMessage != null) {
                ErrorBanner(message = state.errorMessage!!)
                Spacer(Modifier.height(16.dp))
            }

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
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                leadingEmoji = "🔒",
                isPassword = true,
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* TODO forgot password */ }) {
                    Text("Forgot password? 🤔", color = PrimaryGreen, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            HealthEaseButton(
                text = "Sign In 🔓",
                onClick = viewModel::login,
                loading = state.isLoading
            )

            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "  or  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = { /* TODO Google SSO */ },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("🔵  Continue with Google", fontSize = 15.sp)
            }

            Spacer(Modifier.height(28.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Don't have an account? ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Sign Up ✨",
                        color = PrimaryGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}