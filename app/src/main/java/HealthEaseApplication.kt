
package com.example.healthease_part_2

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class HealthEaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
