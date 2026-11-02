
package com.example.healthease_part_2

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }
}
