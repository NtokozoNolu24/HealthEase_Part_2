package com.example.healthease.data.repository

import com.example.healthease.data.local.UserSettingsDao
import com.example.healthease.data.local.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dao: UserSettingsDao) {

    fun observe(userId: String): Flow<UserSettingsEntity?> = dao.observe(userId)

    suspend fun getOrCreate(userId: String): UserSettingsEntity {
        return dao.get(userId) ?: UserSettingsEntity(userId = userId).also {
            dao.upsert(it)
        }
    }

    suspend fun save(settings: UserSettingsEntity) = dao.upsert(settings)

    suspend fun delete(userId: String) = dao.delete(userId)
}