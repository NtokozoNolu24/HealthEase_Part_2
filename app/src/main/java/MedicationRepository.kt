

package com.example.healthease_part_2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MedicationRepository {

    private val database = FirebaseDatabase.getInstance().reference

    private fun medicationReference() =
        database.child("users")
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException(
                        "Please sign in before accessing medication reminders."
                    )
            )
            .child("medicationReminders")

    // Save a medication reminder
    fun saveReminder(
        reminder: MedicationReminder,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val reference = medicationReference()

            val reminderId = reminder.id.ifEmpty {
                reference.push().key
                    ?: throw IllegalStateException(
                        "Could not create reminder ID."
                    )
            }

            reminder.id = reminderId

            reference.child(reminderId)
                .setValue(reminder)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message ?: "Could not save medication reminder."
                    )
                }

        } catch (error: Exception) {
            onError(
                error.message ?: "Unable to save medication reminder."
            )
        }
    }

    // Retrieve medication reminders
    fun getReminders(
        onDataChanged: (List<MedicationReminder>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener? {

        return try {
            val listener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val reminders = snapshot.children.mapNotNull { child ->

                        child.getValue(
                            MedicationReminder::class.java
                        )?.apply {
                            id = child.key ?: id
                        }
                    }

                    onDataChanged(reminders)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            }

            medicationReference().addValueEventListener(listener)

            listener

        } catch (error: Exception) {
            onError(
                error.message ?: "Could not load medication reminders."
            )
            null
        }
    }

    // Delete a medication reminder
    fun removeReminder(
        reminderId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            medicationReference()
                .child(reminderId)
                .removeValue()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message ?: "Could not delete medication reminder."
                    )
                }

        } catch (error: Exception) {
            onError(
                error.message ?: "Unable to delete medication reminder."
            )
        }
    }

    // Remove Firebase listener when screen closes
    fun removeRemindersListener(listener: ValueEventListener) {
        try {
            medicationReference().removeEventListener(listener)
        } catch (error: Exception) {
            // Safely handle listener cleanup.
        }
    }
}
