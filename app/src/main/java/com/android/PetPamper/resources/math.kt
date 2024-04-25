package com.android.PetPamper.resources


import kotlin.math.*

fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6372.8  // Earth radius in kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val originLat = Math.toRadians(lat1)
    val destinationLat = Math.toRadians(lat2)

    val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(originLat) * cos(destinationLat)
    val c = 2 * asin(sqrt(a))

    return r * c
}
