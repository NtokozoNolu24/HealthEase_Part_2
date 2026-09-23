package com.example.healthease.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthCheckDao {

    @Query("SELECT * FROM health_checks WHERE userId = :userId ORDER BY recordedAt DESC")
    fun observeAll(userId: String): Flow<List<HealthCheckEntity>>

    @Query("SELECT * FROM health_checks WHERE userId = :userId AND type = :type ORDER BY recordedAt DESC")
    fun observeByType(userId: String, type: String): Flow<List<HealthCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(check: HealthCheckEntity)

    @Query("DELETE FROM health_checks WHERE checkId = :id")
    suspend fun delete(id: String)
}