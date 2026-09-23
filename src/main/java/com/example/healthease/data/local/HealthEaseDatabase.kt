package com.example.healthease.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        MedicationEntity::class,
        MedicationLogEntity::class,
        AppointmentEntity::class,
        HealthCheckEntity::class,
        NotificationEntity::class,
        UserSettingsEntity::class,
        ArticleEntity::class,       // ← NEW
        BookmarkEntity::class       // ← NEW
    ],
    version = 4,                    // ← bumped
    exportSchema = false
)
abstract class HealthEaseDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun medicationDao(): MedicationDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun healthCheckDao(): HealthCheckDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun articleDao(): ArticleDao        // ← NEW
    abstract fun bookmarkDao(): BookmarkDao      // ← NEW

    companion object {
        @Volatile private var INSTANCE: HealthEaseDatabase? = null

        fun getInstance(context: Context): HealthEaseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HealthEaseDatabase::class.java,
                    "healthease.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}