package com.android.PetPamper.model

data class User(
    val name: String = "",

    val email: String = "",
    val phoneNumber: String = "",
    val address: Address? = null,
    val userId: String  = ""
) {}
