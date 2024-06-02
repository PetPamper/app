package com.android.PetPamper.ui.screen.register

import com.android.PetPamper.database.FirebaseConnection

fun inputValidityAndError(isValid: Boolean, errorMessage: String): Pair<Boolean, String> {
    return Pair(isValid, if (isValid) "" else errorMessage)
}
fun isValidName(name: String): Pair<Boolean, String> {
    val isValid = name.isNotBlank()
    val errorMessage = "Name is blank"
    return inputValidityAndError(isValid, errorMessage)
} // Add more conditions as necessary

fun isValidEmail(email: String): Pair<Boolean, String> {
    val isValid = email.contains('@') && email.contains('.')
    val errorMessage = "Email is invalid"
    return inputValidityAndError(isValid, errorMessage)
} // Simplified validation

fun isValidEmail1(email: String): Pair<Boolean, String> {
    val firebaseConnection = FirebaseConnection.getInstance()
    var alreadyinuse = false
    var isValid = false
    firebaseConnection
        .verifyEmail(email, "groomer")
        .addOnCompleteListener { isExist ->
            if (isExist.isSuccessful) {
                val emailExists = isExist.result
                if (emailExists) {
                    alreadyinuse = true
                } else {
                    alreadyinuse = false
                }
            } else {
                alreadyinuse = false
            }
        }
        .addOnFailureListener { alreadyinuse = true }
    isValid = email.contains('@') && email.contains('.') && !alreadyinuse

    return inputValidityAndError(isValid, if (!alreadyinuse) "Email is invalid" else "Email is already in use")
}

// More robust validation
fun isValidPhone(phone: String): Pair<Boolean, String> {
    var _phone = phone.replace(Regex("-|\\s"), "")
    if (_phone.startsWith("+")) {
        _phone = _phone.replaceFirst("+", "00")
    }
    val isValid = _phone.matches(Regex("\\d*")) && phone.isNotBlank()
    val errorMessage = "Phone number is invalid"
    return inputValidityAndError(isValid, errorMessage)
}

fun isValidPassword(password: String): Pair<Boolean, String> {
    val isValid = password.length >= 8
    val errorMessage = "Password must have at least 8 characters"
    return inputValidityAndError(isValid, errorMessage)
} // Basic condition for demonstration