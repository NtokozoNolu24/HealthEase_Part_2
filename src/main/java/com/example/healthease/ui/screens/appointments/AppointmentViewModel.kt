package com.example.healthease.ui.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.AppointmentEntity
import com.example.healthease.data.repository.AppointmentRepository
import com.example.healthease.data.repository.NotificationRepository
import com.example.healthease.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppointmentUiState(
    val upcoming: List<AppointmentEntity> = emptyList(),
    val all: List<AppointmentEntity> = emptyList(),
    val savedMessage: String? = null
)

class AppointmentViewModel(
    private val repo: AppointmentRepository,
    private val notifRepo: NotificationRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentUiState())
    val state: StateFlow<AppointmentUiState> = _state.asStateFlow()

    init {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.observeUpcoming(uid).collect { list ->
                    _state.update { it.copy(upcoming = list) }
                }
            }
            viewModelScope.launch {
                repo.observeAll(uid).collect { list ->
                    _state.update { it.copy(all = list) }
                }
            }
        }
    }

    fun add(a: AppointmentEntity) {
        viewModelScope.launch {
            repo.add(a)
            notifRepo.notify(
                userId = a.userId,
                type = "reminder",
                title = "📅 Appointment added",
                body = "${a.doctorName} on ${DateTimeUtils.formatDateTime(a.appointmentDate)}"
            )
            _state.update { it.copy(savedMessage = "Appointment saved ✅") }
        }
    }

    fun updateStatus(id: String, status: String) {
        viewModelScope.launch { repo.updateStatus(id, status) }
    }

    fun delete(id: String) {
        viewModelScope.launch { repo.delete(id) }
    }

    fun clearMessage() = _state.update { it.copy(savedMessage = null) }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AppointmentViewModel(
                    app.appointmentRepository,
                    app.notificationRepository,
                    app
                ) as T
            }
        }
    }
}