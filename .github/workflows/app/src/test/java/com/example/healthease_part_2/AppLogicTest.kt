package com.example.healthease_part_2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLogicTest {

    @Test
    fun supportedLanguages_includesAllFour() {
        assertEquals(4, UserSession.LANGUAGES.size)
        assertTrue(UserSession.LANGUAGES.contains("English"))
        assertTrue(UserSession.LANGUAGES.contains("isiZulu"))
        assertTrue(UserSession.LANGUAGES.contains("Afrikaans"))
        assertTrue(UserSession.LANGUAGES.contains("isiXhosa"))
    }

    @Test
    fun textSizes_areSmallMediumLarge() {
        assertEquals(listOf("Small", "Medium", "Large"), UserSession.TEXT_SIZES)
    }

    @Test
    fun medicationReminder_storesValues() {
        val reminder = MedicationReminder(
            id = "1",
            medicationName = "Panado",
            time = "08:00",
            repeat = "Daily"
        )
        assertEquals("Panado", reminder.medicationName)
        assertEquals("08:00", reminder.time)
        assertEquals("Daily", reminder.repeat)
    }

    @Test
    fun appointmentReminder_defaultsAreEmpty() {
        val appointment = AppointmentReminder()
        assertEquals("", appointment.id)
        assertEquals("", appointment.name)
    }
}
