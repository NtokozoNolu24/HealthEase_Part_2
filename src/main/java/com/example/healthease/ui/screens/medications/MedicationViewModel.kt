package com.example.healthease.ui.screens.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthease.HealthEaseApplication
import com.example.healthease.data.local.MedicationEntity
import com.example.healthease.data.local.MedicationLogEntity
import com.example.healthease.data.repository.MedicationRepository
import com.example.healthease.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationUiState(
    val medications: List<MedicationEntity> = emptyList(),
    val logs: List<MedicationLogEntity> = emptyList(),
    val savedMessage: String? = null
) {
    /** Adherence % across all logs in last 30 days. */
    fun adherencePercent(): Int {
        val recent = logs.filter {
            it.scheduledTime > System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        }
        if (recent.isEmpty()) return 100
        val taken = recent.count { it.status == "taken" }
        return ((taken.toFloat() / recent.size) * 100).toInt()
    }
}

class MedicationViewModel(
    private val repo: MedicationRepository,
    private val notifRepo: NotificationRepository,
    private val app: HealthEaseApplication
) : ViewModel() {

    private val _state = MutableStateFlow(MedicationUiState())
    val state: StateFlow<MedicationUiState> = _state.asStateFlow()

    init {
        val uid = app.currentUserId
        if (uid != null) {
            viewModelScope.launch {
                repo.observeActive(uid).collect { list ->
                    _state.update { it.copy(medications = list) }
                }
            }
            viewModelScope.launch {
                repo.observeLogs(uid).collect { list ->
                    _state.update { it.copy(logs = list) }
                }
            }
        }
    }

    fun add(med: MedicationEntity) {
        viewModelScope.launch {
            repo.add(med)
            notifRepo.notify(
                userId = med.userId,
                type = "reminder",
                title = "💊 Medication added",
                body = "${med.name} — ${med.dosage}"
            )
            _state.update { it.copy(savedMessage = "Medication added ✅") }
        }
    }

    fun deactivate(id: String) {
        viewModelScope.launch { repo.deactivate(id) }
    }

    fun delete(id: String) {
        viewModelScope.launch { repo.delete(id) }
    }

    fun logDose(med: MedicationEntity, taken: Boolean, reason: String? = null) {
        viewModelScope.launch {
            repo.logDose(
                MedicationLogEntity(
                    medicationId = med.medicationId,
                    userId = med.userId,
                    scheduledTime = System.currentTimeMillis(),
                    takenAt = if (taken) System.currentTimeMillis() else null,
                    status = if (taken) "taken" else "skipped",
                    skipReason = reason
                )
            )
            _state.update {
                it.copy(savedMessage = if (taken) "Marked as taken ✅" else "Marked as skipped")
            }
        }
    }

    fun clearMessage() = _state.update { it.copy(savedMessage = null) }

    companion object {
        fun factory(app: HealthEaseApplication) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MedicationViewModel(
                    app.medicationRepository,
                    app.notificationRepository,
                    app
                ) as T
            }
        }
    }
}