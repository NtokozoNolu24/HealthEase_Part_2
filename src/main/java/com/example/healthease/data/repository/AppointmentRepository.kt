package com.example.healthease.data.repository

import com.example.healthease.data.local.AppointmentDao
import com.example.healthease.data.local.AppointmentEntity
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val dao: AppointmentDao) {
    fun observeUpcoming(userId: String): Flow<List<AppointmentEntity>> =
        dao.observeUpcoming(userId, System.currentTimeMillis())
    fun observeAll(userId: String): Flow<List<AppointmentEntity>> = dao.observeAll(userId)
    suspend fun add(a: AppointmentEntity) = dao.insert(a)
    suspend fun update(a: AppointmentEntity) = dao.update(a)
    suspend fun delete(id: String) = dao.delete(id)
    suspend fun updateStatus(id: String, status: String) = dao.updateStatus(id, status)
}