package com.android.PetPamper.model

data class Reservation(
    var userId: String,
    var city: String,
    var date: String,
    var time: String,
    var capabilities: List<String>,
    var km: Int
) {}
