package com.android.PetPamper.ui.screen.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection

class LoginViewModel(
    val asGroomer: Boolean = false,
    val onForgotPassword: () -> Unit,
    val onSuccessfulLogin: (Map<String, Any>) -> Unit,
    val onFailedLogin: (Exception) -> Unit) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    //var login by mutableStateOf(true)

    val firebaseConnection = FirebaseConnection.getInstance()

    val topText = "Please enter your credentials"

    val emailLabel = "Email"
    val passwordLabel = "Password"

    val forgotPasswordText = "Forgot password?"

    var showError by mutableStateOf(false)
    var errorMessage by mutableStateOf("Login failed, email or password is incorrect")

    fun onCLick() {
        errorMessage = ""
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email or password is blank"
            showError = true
        } else {
            firebaseConnection.loginUser(
                email,
                password,
                onSuccess = {
                    firebaseConnection.fetchData(
                        collectionPath = if (asGroomer) "groomers" else "users",
                        document = email,
                        onSuccess = {
                            showError = false
                            onSuccessfulLogin(it)
//                            Log.d(
//                                "Firebase query",
//                                "User found," + " name is ${document.get("name")}")
//                            viewModel.name = document.get("name").toString()
//                            viewModel.email = document.get("email").toString()
//                            viewModel.phoneNumber = document.get("phoneNumber").toString()
//                            registeredAsGroomer = true
//                            currentStep = 4
                        },
                        onFailure = { exception ->
                            errorMessage = "Couldn't communicate with Authentication server"
                            showError = true
                            Log.e("Firebase query", "Get failed with ", exception)
                            onFailedLogin(exception)
                        })
                            },
                onFailure = { exception ->
                    errorMessage = "Login failed, email or password is incorrect"
                    showError = true
                    Log.e("Firebase query", "Get failed with ", exception)
                    onFailedLogin(exception)
                })
        }
    }
}