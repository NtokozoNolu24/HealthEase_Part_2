package com.example.healthease.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.UserSettingsEntity
import com.example.healthease.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: UserSettingsEntity? = null,
    val isLoading: Boolean = true,
    val savedMessage: String? = null
)

class SettingsViewModel(
    private val repo: SettingsRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.getOrCreate(uid)
                repo.observe(uid).collect { s ->
                    _state.update { it.copy(settings = s, isLoading = false) }
                }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun update(transform: (UserSettingsEntity) -> UserSettingsEntity) {
        val current = _state.value.settings ?: return
        val updated = transform(current).copy(updatedAt = System.currentTimeMillis())
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
                return SettingsViewModel(app.settingsRepository, app) as T
            }
        }
    }
}