package com.android.PetPamper.model

data class Groomer(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: Address = Address("", "", "", "", LocationMap()),
    val yearsExperience: String = "", // change to int after adding checks
    val services: List<String> = listOf(""),
    val petTypes: List<String> = listOf(""),
    val profilePic: String = "",
    val price: Int = 0,
) {}
