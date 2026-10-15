package com.example.healthease.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointments WHERE userId = :userId AND appointmentDate >= :now ORDER BY appointmentDate ASC")
    fun observeUpcoming(userId: String, now: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY appointmentDate DESC")
    fun observeAll(userId: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appointment: AppointmentEntity)

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Query("DELETE FROM appointments WHERE appointmentId = :id")
    suspend fun delete(id: String)

    @Query("UPDATE appointments SET status = :status WHERE appointmentId = :id")
    suspend fun updateStatus(id: String, status: String)
}