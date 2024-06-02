package com.android.PetPamper.ui.screen.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class SignUpViewModel(val navController: NavController, var hasGroomerAccount: Boolean = false): ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var address by mutableStateOf(Address("", "", "", "", LocationMap()))
    var password by mutableStateOf("")
    var locationMap: LocationMap = LocationMap()

    val firebaseConnection = FirebaseConnection.getInstance()
    val db = Firebase.firestore

    val NUM_STEPS = 6

    val initialStep = if (!hasGroomerAccount) 1 else 10
    var currentStep by mutableIntStateOf(initialStep)

    var registeredAsGroomer by mutableStateOf(false)

    val shownTexts = listOf(
        "Let’s start with your name",
        "Enter your email",
        "What’s your phone number?",
        "Enter your Address",
        "Great! Create your password",
        "Confirm your password"
    )
    val fieldNames = listOf(
        "Name", "Email", "Phone", "Street", "Password", "Confirm Password"
    )
    val inputCheckMap = mapOf(
        "Name" to ::isValidName,
        "Email" to ::isValidEmail,
        "Phone" to ::isValidPhone,
        "Password" to ::isValidPassword,
        "Confirm Password" to { input -> Pair(input == password, "Passwords do not match") }
    )

    var curTextShown by mutableStateOf(shownTexts[currentStep])
    var curFieldName by mutableStateOf(fieldNames[currentStep])

    var textField by mutableStateOf("")
    var extraTextField = mutableStateListOf<String>()

    var errorText by mutableStateOf("")

    val loginViewModel = LoginViewModel(
        asGroomer = true,
        onForgotPassword = { onForgotPassword() },
        onSuccessfulLogin = { user ->
            Log.d(
                "Firebase query",
                "User found," + " name is ${user.get("name")}")
            name = user["name"].toString()
            email = user["email"].toString()
            phoneNumber = user["phoneNumber"].toString()
            registeredAsGroomer = true
            currentStep = 4
        },
        onFailedLogin = {})

    fun onNext() {
        val inputChecker = inputCheckMap.getOrElse(curFieldName) { { input: String -> Pair(true, "") } }
        val temp = inputChecker(textField)

        val isValidInput = temp.first
        errorText = temp.second

        if (isValidInput) {
            when (currentStep) {
                1 -> { name = textField }
                2 -> { email = textField }
                3 -> { phoneNumber = textField }
                4 -> {
                    address.street = textField
                    address.city = extraTextField[0]
                    address.state = extraTextField[1]
                    address.postalCode = extraTextField[2]
                    address.location.latitude = locationMap.latitude
                    address.location.longitude = locationMap.longitude
                    address.location.name = locationMap.name
                    if (registeredAsGroomer) {
                        currentStep += 2
                    }
                }
                5 -> { password = textField }
            }

            currentStep++

            curTextShown = shownTexts[currentStep]
            curFieldName = fieldNames[currentStep]
            textField = ""
            errorText = ""
        }
    }

    fun register() {
        if (!registeredAsGroomer) {
            firebaseConnection.registerUser(
                this.email,
                this.password,
                onSuccess = {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    firebaseConnection.addUser(
                        User(
                            this.name,
                            this.email,
                            this.phoneNumber,
                            this.address,
                            0,
                            "",
                            uid),
                        onSuccess = { currentStep++ },
                        onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
                },
                onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
        } else {
            // Need to check that user wasn't already registered to avoid duplicate accounts
            val userRef = db.collection("users").document(this.email)
            userRef
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        firebaseConnection.addUser(
                            User(
                                this.name,
                                this.email,
                                this.phoneNumber,
                                this.address,
                                0,
                                "",
                                uid),
                            onSuccess = { currentStep++ },
                            onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
                    } else {
                        Log.e("AlreadyRegistered", "user was already registered")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firebase query", "Get failed with ", exception)
                }
        }
    }
    fun onRegistrationSuccess() {
        navController.navigate("LoginScreen")
    }

    fun onForgotPassword() {
        navController.navigate("EmailScreen")
    }
}