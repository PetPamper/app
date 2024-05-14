package com.android.PetPamper.model

data class Reservation(
    val reservationId: String,
    val groomerEmail: String,
    val userEmail: String,
    val date: String,
    val hour: String
) {}
