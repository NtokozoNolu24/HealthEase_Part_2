package com.example.healthease.data.repository

import com.example.healthease.data.local.ProfileDao
import com.example.healthease.data.local.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val dao: ProfileDao) {
    fun observe(userId: String): Flow<ProfileEntity?> = dao.observeProfile(userId)
    suspend fun get(userId: String): ProfileEntity? = dao.getProfile(userId)
    suspend fun save(profile: ProfileEntity) = dao.upsert(profile)
}