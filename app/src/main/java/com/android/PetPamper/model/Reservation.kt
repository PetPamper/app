package com.android.PetPamper.model

data class Reservation(
    val reservationId: String = "",
    val groomerEmail: String = "",
    val price: String = "",
    val services: String = "",
    val groomerName: String = "",
    val userEmail: String = "",
    val experienceYears: String = "",
    val date: String = "",
    val hour: String = "",
) {}
