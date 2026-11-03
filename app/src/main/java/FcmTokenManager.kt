
package com.example.healthease_part_2

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

object FcmTokenManager {

    private const val TAG = "FcmTokenManager"

    // Retrieve the current device's FCM token.
    fun saveCurrentToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e(TAG, "Could not retrieve FCM token", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result

                if (!token.isNullOrEmpty()) {
                    saveToken(token)
                }
            }
    }

    // Save the token under the currently authenticated user.
    fun saveToken(token: String) {

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.w(TAG, "No signed-in user. Token was not saved.")
            return
        }

        val encodedToken = Base64.encodeToString(
            token.toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )

        val tokenData = mapOf(
            "token" to token,
            "device" to android.os.Build.MODEL
        )

        FirebaseDatabase.getInstance().reference
            .child("users")
            .child(user.uid)
            .child("fcmTokens")
            .child(encodedToken)
            .setValue(tokenData)
            .addOnSuccessListener {
                Log.d(TAG, "FCM token saved successfully")
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to save FCM token", error)
            }
    }
}
