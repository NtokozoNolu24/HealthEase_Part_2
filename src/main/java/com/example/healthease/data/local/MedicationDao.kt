package com.example.healthease.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medications WHERE userId = :userId AND isActive = 1 ORDER BY name ASC")
    fun observeActive(userId: String): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAll(userId: String): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(med: MedicationEntity)

    @Update
    suspend fun update(med: MedicationEntity)

    @Query("UPDATE medications SET isActive = 0 WHERE medicationId = :id")
    suspend fun deactivate(id: String)

    @Query("DELETE FROM medications WHERE medicationId = :id")
    suspend fun delete(id: String)

    // Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logDose(log: MedicationLogEntity)

    @Query("SELECT * FROM medication_logs WHERE userId = :userId ORDER BY scheduledTime DESC LIMIT 200")
    fun observeLogs(userId: String): Flow<List<MedicationLogEntity>>

    @Query("SELECT * FROM medication_logs WHERE medicationId = :medId ORDER BY scheduledTime DESC")
    fun observeLogsForMed(medId: String): Flow<List<MedicationLogEntity>>
}