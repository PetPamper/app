package com.android.PetPamper.model

data class User(
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var address: Address = Address(),
    var pawPoints: Int = 0,
    var profilePictureUrl: String  = "",
    val userId: String = ""
) {}
