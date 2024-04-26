package com.android.PetPamper.model

import android.health.connect.datatypes.ExerciseRoute.Location

data class LocationMap(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var name: String = "Default Location"
) {}

data class Address(
    var street: String= "",
    var city: String = "",
    var state: String = "",
    var postalCode: String = "",
    var location : LocationMap = LocationMap()
) {}
