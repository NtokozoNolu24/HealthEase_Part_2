package com.example.healthease.ui.screens.medicalid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.ProfileEntity
import com.example.healthease.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicalIdUiState(
    val profile: ProfileEntity? = null,
    val isLoading: Boolean = true,
    val savedMessage: String? = null
)

class MedicalIdViewModel(
    private val repo: ProfileRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(MedicalIdUiState())
    val state: StateFlow<MedicalIdUiState> = _state.asStateFlow()

    init {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.observe(uid).collect { profile ->
                    _state.update { it.copy(profile = profile, isLoading = false) }
                }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun save(updated: ProfileEntity) {
        viewModelScope.launch {
            repo.save(updated)
            _state.update { it.copy(savedMessage = "Saved ✅") }
        }
    }

    fun clearMessage() = _state.update { it.copy(savedMessage = null) }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MedicalIdViewModel(app.profileRepository, app) as T
            }
        }
    }
}