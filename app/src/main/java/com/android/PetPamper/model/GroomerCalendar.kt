package com.android.PetPamper.model

import java.util.Calendar

data class GroomerAvailability(
    val email: String, // Primary key
    val availableHours: List<Calendar>
)
