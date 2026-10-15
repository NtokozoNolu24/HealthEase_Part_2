package com.example.healthease.ui.screens.register

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

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val userName: String = ""
)

class RegisterViewModel(
    private val repo: UserRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onFullNameChange(v: String) = _state.update {
        it.copy(fullName = v, fullNameError = null, errorMessage = null)
    }
    fun onEmailChange(v: String) = _state.update {
        it.copy(email = v, emailError = null, errorMessage = null)
    }
    fun onPhoneChange(v: String) = _state.update {
        it.copy(phone = v, phoneError = null, errorMessage = null)
    }
    fun onPasswordChange(v: String) = _state.update {
        it.copy(password = v, passwordError = null, errorMessage = null)
    }
    fun onConfirmPasswordChange(v: String) = _state.update {
        it.copy(confirmPassword = v, confirmPasswordError = null, errorMessage = null)
    }

    fun register() {
        val s = _state.value

        val nameErr = if (s.fullName.trim().length < 2) "Enter your full name 👤" else null
        val emailErr = if (!Validation.isValidEmail(s.email)) "Enter a valid email 📧" else null
        val phoneErr = if (s.phone.isNotBlank() && !Validation.isValidSaPhone(s.phone))
            "Use SA format e.g. 0821234567 📱" else null
        val passErr = Validation.passwordError(s.password)
        val confirmErr = if (s.password != s.confirmPassword)
            "Passwords don't match 🔁" else null

        if (listOf(nameErr, emailErr, phoneErr, passErr, confirmErr).any { it != null }) {
            _state.update {
                it.copy(
                    fullNameError = nameErr,
                    emailError = emailErr,
                    phoneError = phoneErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repo.register(
                fullName = s.fullName,
                email = s.email,
                password = s.password,
                phone = s.phone.ifBlank { null }
            )
            when (result) {
                is AuthResult.Success -> {
                    // ✅ Store session so Medical ID / Meds / Appointments
                    // know which user is currently logged in
                    app.setCurrentUser(result.user.userId)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            registerSuccess = true,
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
                return RegisterViewModel(app.userRepository, app) as T
            }
        }
    }
}