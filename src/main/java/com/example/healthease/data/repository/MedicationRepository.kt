package com.example.healthease.data.repository

import com.example.healthease.data.local.MedicationDao
import com.example.healthease.data.local.MedicationEntity
import com.example.healthease.data.local.MedicationLogEntity
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val dao: MedicationDao) {

    fun observeActive(userId: String): Flow<List<MedicationEntity>> = dao.observeActive(userId)
    fun observeAll(userId: String): Flow<List<MedicationEntity>> = dao.observeAll(userId)
    fun observeLogs(userId: String): Flow<List<MedicationLogEntity>> = dao.observeLogs(userId)
    fun observeLogsFor(medId: String): Flow<List<MedicationLogEntity>> = dao.observeLogsForMed(medId)

    suspend fun add(med: MedicationEntity) = dao.insert(med)
    suspend fun update(med: MedicationEntity) = dao.update(med)
    suspend fun deactivate(id: String) = dao.deactivate(id)
    suspend fun delete(id: String) = dao.delete(id)
    suspend fun logDose(log: MedicationLogEntity) = dao.logDose(log)
}