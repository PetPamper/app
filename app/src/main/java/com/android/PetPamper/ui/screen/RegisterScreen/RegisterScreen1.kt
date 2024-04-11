package com.android.PetPamper.ui.screen.RegisterScreen


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.User

import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding


class SignUpViewModel() {

  var name by mutableStateOf("")
  var email by mutableStateOf("")
  var phoneNumber by mutableStateOf("")
  var address by mutableStateOf(Address("", "", "", ""))
  var password by mutableStateOf("")
}

@Composable
fun SignUpScreen(viewModel: SignUpViewModel, navController: NavController) {
  var currentStep by remember { mutableStateOf(1) }

  var confirmPassword by remember { mutableStateOf("") }

  when (currentStep) {
    1 ->
        SignUpScreenLayout(
            1,
            false,
            "Let’s start with your name",
            "Name",
            viewModel,
            onNext = { newName ->
              viewModel.name = newName
              currentStep++
            })
    2 ->
        SignUpScreenLayout(
            2,
            false,
            "Hello ${viewModel.name}, enter your email",
            "Email",
            viewModel,
            onNext = { newEmail ->
              viewModel.email = newEmail
              currentStep++
            })
    3 ->
        SignUpScreenLayout(
            3,
            false,
            "What’s your phone number?",
            "Phone Number",
            viewModel,
            onNext = { newPhoneNumber ->
              viewModel.phoneNumber = newPhoneNumber
              currentStep++
            })
    4 ->
        SignUpScreenLayout(
            4,
            true,
            "Enter your Address?",
            "Street",
            viewModel,
            onNextAddress = { street, city, state, postalcode ->

              viewModel.address.city = city
              viewModel.address.state = state
              viewModel.address.street = street
              viewModel.address.postalCode = postalcode
              currentStep++

            })
    5 ->
        SignUpScreenLayout(
            5,
            false,
            "Great! Create your password",
            "Password",
            viewModel,
            onNext = { password ->
              viewModel.password = password
              currentStep++
            })
    6 -> {
      SignUpScreenLayout(
          6,
          false,
          "Confirm your password",
          "Confirm Password",
          viewModel,
          confirmPassword = viewModel.password,
          onNext = { confirmedPassword ->
            if (viewModel.password == confirmedPassword) {

              val firebaseConnection = FirebaseConnection()

              firebaseConnection.registerUser(
                  viewModel.email,
                  viewModel.password,

                  onSuccess = {
                    firebaseConnection.addUser(
                        User(
                            viewModel.name,
                            viewModel.email,
                            viewModel.phoneNumber,
                            viewModel.address),
                        onSuccess = { currentStep++ },
                        onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
                  },
                  onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
            } else {
              // Show error message
            }
          })
    }
    7 -> navController.navigate("LoginScreen")
  }

  // Add more steps as needed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenLayout(
    currentStep: Int,
    isAddress: Boolean,
    textShown: String,
    fieldname: String,
    // Add this line
    viewModel: SignUpViewModel,
    confirmPassword: String? = null,
    onNext: ((String) -> Unit)? = null,
    onNextAddress: ((String, String, String, String) -> Unit)? = null
) {

  var textField by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("") }
  var state by remember { mutableStateOf("") }
  var postaleCode by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf("") }

  val imeVisible = LocalWindowInsets.current.ime.isVisible
  val keyboardOpenState = remember { mutableStateOf(false) }

  keyboardOpenState.value = imeVisible

  val proceedWithNext = {
    var isValidInput = true

    when (fieldname) {
      "Name" ->
          if (!isValidName(textField)) {
            errorText = "Please enter a valid name."
            isValidInput = false
          }
      "Email" ->
          if (!isValidEmail(textField)) {
            errorText = "Please enter a valid email."
            isValidInput = false
          }
      "Password" ->
          if (!isValidPassword(textField)) {
            errorText = "Password must be at least 8 characters."
            isValidInput = false
          }
      "Confirm Password" ->
          if (textField != confirmPassword) {
            errorText = "Passwords do not match."
            isValidInput = false
          }

    // Add more cases as necessary for other fields
    }

    if (isValidInput) {
      errorText = ""
      if (isAddress) {
        onNextAddress?.invoke(textField, city, state, postaleCode)
      } else {
        onNext?.invoke(textField)
      }
    }
  }
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

      Column(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = textShown,
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF2490DF),
                        textAlign = TextAlign.Center,
                    ))


            OutlinedTextField(
                value = textField,
                onValueChange = { textField = it },
                label = { Text(fieldname) },
                singleLine = true,

                visualTransformation =
                    if (fieldname == "Password" || fieldname == "Confirm Password")
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =
                            Color(0xFF2491DF), // Border color when the TextField is focused
                        focusedLabelColor =
                            Color(0xFF2491DF), // Label color when the TextField is focused
                        unfocusedBorderColor =
                            Color.Gray, // Additional customization for other states
                        unfocusedLabelColor = Color.Gray))

            if (errorText.isNotBlank()) {
              Text(
                  text = errorText,
                  color = MaterialTheme.colorScheme.error,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(top = 4.dp))
            }

            if (isAddress) {

              OutlinedTextField(
                  value = city,
                  onValueChange = { city = it },
                  label = { Text("city") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor =
                              Color(0xFF2491DF), // Border color when the TextField is focused
                          focusedLabelColor =
                              Color(0xFF2491DF), // Label color when the TextField is focused
                          unfocusedBorderColor =
                              Color.Gray, // Additional customization for other states
                          unfocusedLabelColor = Color.Gray))

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                  value = state,
                  onValueChange = { state = it },
                  label = { Text("State") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor =
                              Color(0xFF2491DF), // Border color when the TextField is focused
                          focusedLabelColor =
                              Color(0xFF2491DF), // Label color when the TextField is focused
                          unfocusedBorderColor =
                              Color.Gray, // Additional customization for other states
                          unfocusedLabelColor = Color.Gray))

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                  value = postaleCode,
                  onValueChange = { postaleCode = it },
                  label = { Text("Postal Code") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor =
                              Color(0xFF2491DF), // Border color when the TextField is focused
                          focusedLabelColor =
                              Color(0xFF2491DF), // Label color when the TextField is focused
                          unfocusedBorderColor =
                              Color.Gray, // Additional customization for other states
                          unfocusedLabelColor = Color.Gray))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier =
                    Modifier.fillMaxSize() // Apply background for the entire screen if necessary
                ) {
                  Column(
                      modifier =
                          if (textField.isNotBlank()) {
                            Modifier.align(
                                    Alignment
                                        .Center) // Align the Column to the bottom-center of the
                                // Box
                                .fillMaxWidth() // The Column should fill the maximum width of the
                                // Box
                                .navigationBarsWithImePadding() // Apply padding for navigation bar
                                // and IME // The light gray
                                // background for the Column
                                .padding(16.dp)
                          } else {
                            Modifier.align(
                                    Alignment
                                        .BottomCenter) // Align the Column to the bottom-center of
                                // the
                                // Box
                                .fillMaxWidth() // The Column should fill the maximum width of the
                                // Box
                                .navigationBarsWithImePadding() // Apply padding for navigation bar
                                // and IME // The light gray
                                // background for the Column
                                .padding(16.dp)
                          },
                      verticalArrangement = Arrangement.Center,
                      horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = proceedWithNext,
                            modifier =
                                Modifier.wrapContentWidth(), // Make the button wrap its content
                            colors =
                                ButtonDefaults.buttonColors( // Set the button's background color
                                    containerColor = Color(0xFF2491DF))) {
                              Icon(
                                  imageVector = Icons.Filled.ArrowForward,
                                  contentDescription = "Go forward",
                                  tint = Color.White // Set the icon color to blue
                                  )
                            }

                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp)) // This adds space between the button and the progress
                        // bar

                        val progress = currentStep.toFloat() / 5
                        LinearProgressIndicator(
                            progress = { progress },
                            color = Color(0xFF2491DF),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(10.dp)))
                      }
                }
          }
    }
  }
}

fun isValidName(name: String) = name.isNotBlank() // Add more conditions as necessary

fun isValidEmail(email: String) =
    email.contains('@') && email.contains('.') // Simplified validation

fun isValidPassword(password: String) = password.length >= 8 // Basic condition for demonstration


@Preview
@Composable
fun SignUpScreenPreview() {
  val viewModel = remember { SignUpViewModel() } // In actual app, provide this via ViewModel

  val navController = rememberNavController()
  SignUpScreen(viewModel, navController)

}
