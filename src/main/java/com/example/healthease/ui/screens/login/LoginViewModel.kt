package com.example.healthease.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.repository.AuthResult
import com.example.healthease.data.repository.UserRepository
import com.example.healthease.util.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val userName: String = ""
)

class LoginViewModel(
    private val repo: UserRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(v: String) = _state.update {
        it.copy(email = v, emailError = null, errorMessage = null)
    }

    fun onPasswordChange(v: String) = _state.update {
        it.copy(password = v, passwordError = null, errorMessage = null)
    }

    fun login() {
        val s = _state.value
        val emailErr = if (!Validation.isValidEmail(s.email)) "Enter a valid email 📧" else null
        val passErr = if (s.password.isBlank()) "Password is required 🔒" else null

        if (emailErr != null || passErr != null) {
            _state.update { it.copy(emailError = emailErr, passwordError = passErr) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.login(s.email, s.password)) {
                is AuthResult.Success -> {
                    // ✅ Store session so Medical ID / Meds / Appointments
                    // know which user is currently logged in
                    app.setCurrentUser(result.user.userId)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userName = result.user.fullName
                        )
                    }
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(app.userRepository, app) as T
            }
        }
    }
}