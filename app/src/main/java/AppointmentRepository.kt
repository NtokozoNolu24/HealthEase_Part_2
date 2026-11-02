package com.example.healthease_part_2

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppointmentRepository {

    private val database = FirebaseDatabase.getInstance().reference

    private fun appointmentReference() =
        database.child("users")
            .child(
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException(
                        "Please sign in before accessing appointments."
                    )
            )
            .child("appointmentReminders")

    fun saveAppointment(
        appointment: AppointmentReminder,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val reference = appointmentReference()

            val appointmentId = appointment.id.ifEmpty {
                reference.push().key
                    ?: throw IllegalStateException(
                        "Could not create appointment ID."
                    )
            }

            appointment.id = appointmentId

            reference.child(appointmentId)
                .setValue(appointment)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message ?: "Could not save appointment."
                    )
                }

        } catch (error: Exception) {
            onError(
                error.message ?: "Unable to save appointment."
            )
        }
    }

    fun getAppointments(
        onDataChanged: (List<AppointmentReminder>) -> Unit,
        onError: (String) -> Unit
    ): ValueEventListener? {

        return try {
            val listener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val appointments =
                        snapshot.children.mapNotNull { child ->

                            child.getValue(
                                AppointmentReminder::class.java
                            )?.apply {
                                id = child.key ?: id
                            }
                        }

                    onDataChanged(appointments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            }

            appointmentReference().addValueEventListener(listener)
            listener

        } catch (error: Exception) {
            onError(
                error.message ?: "Could not load appointments."
            )
            null
        }
    }

    fun removeAppointment(
        appointmentId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            appointmentReference()
                .child(appointmentId)
                .removeValue()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { error ->
                    onError(
                        error.message ?: "Could not delete appointment."
                    )
                }

        } catch (error: Exception) {
            onError(
                error.message ?: "Unable to delete appointment."
            )
        }
    }

    fun removeAppointmentsListener(listener: ValueEventListener) {
        try {
            appointmentReference().removeEventListener(listener)
        } catch (error: Exception) {
            // Safely handle listener cleanup.
        }
    }
}