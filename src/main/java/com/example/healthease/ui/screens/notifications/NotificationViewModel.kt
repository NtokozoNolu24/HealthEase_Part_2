package com.example.healthease.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.NotificationEntity
import com.example.healthease.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<NotificationEntity> = emptyList(),
    val unreadCount: Int = 0
)

class NotificationViewModel(
    private val repo: NotificationRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationUiState())
    val state: StateFlow<NotificationUiState> = _state.asStateFlow()

    init {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.observeAll(uid).collect { list ->
                    _state.update { it.copy(notifications = list) }
                }
            }
            viewModelScope.launch {
                repo.observeUnread(uid).collect { n ->
                    _state.update { it.copy(unreadCount = n) }
                }
            }
        }
    }

    fun markRead(id: String) {
        viewModelScope.launch { repo.markRead(id) }
    }

    fun markAllRead() {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch { repo.markAllRead(uid) }
        }
    }

    fun clearAll() {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch { repo.clearAll(uid) }
        }
    }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NotificationViewModel(app.notificationRepository, app) as T
            }
        }
    }
}