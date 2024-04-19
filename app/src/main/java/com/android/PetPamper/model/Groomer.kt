package com.android.PetPamper.model

data class Groomer (
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: Address,
    val yearsExperience: String, // change to int after adding checks
    val services: List<String>
) {}
