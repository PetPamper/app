package com.android.PetPamper.ui.screen.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.User

class SignUpViewModel {

  var name by mutableStateOf("")
  var email by mutableStateOf("")
  var phoneNumber by mutableStateOf("")
  var address by mutableStateOf(Address("", "", "", ""))
  var password by mutableStateOf("")
}

@Composable
fun Register(viewModel: SignUpViewModel, navController: NavController) {
  var currentStep by remember { mutableIntStateOf(1) }

  when (currentStep) {
    1 ->
        RegisterLayout(
            1,
            false,
            "Let’s start with your name",
            "Name",
            onNext = { newName ->
              viewModel.name = newName
              currentStep++
            })
    2 ->
        RegisterLayout(
            2,
            false,
            "Hello ${viewModel.name}, enter your email",
            "Email",
            onNext = { newEmail ->
              viewModel.email = newEmail
              currentStep++
            })
    3 ->
        RegisterLayout(
            3,
            false,
            "What’s your phone number?",
            "Phone Number",
            onNext = { newPhoneNumber ->
              viewModel.phoneNumber = newPhoneNumber
              currentStep++
            })
    4 ->
        RegisterLayout(
            4,
            true,
            "Enter your Address?",
            "Street",
            onNextAddress = { street, city, state, postalCode ->
              viewModel.address.city = city
              viewModel.address.state = state
              viewModel.address.street = street
              viewModel.address.postalCode = postalCode
              currentStep++
            })
    5 ->
        RegisterLayout(
            5,
            false,
            "Great! Create your password",
            "Password",
            onNext = { password ->
              viewModel.password = password
              currentStep++
            })
    6 -> {
      RegisterLayout(
          6,
          false,
          "Confirm your password",
          "Confirm Password",
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

@Composable
fun RegisterLayout(
    currentStep: Int,
    isAddress: Boolean,
    textShown: String,
    fieldName: String,
    confirmPassword: String? = null,
    onNext: ((String) -> Unit)? = null,
    onNextAddress: ((String, String, String, String) -> Unit)? = null
) {

  var textField by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("") }
  var state by remember { mutableStateOf("") }
  var postalCode by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf("") }

  fun proceedWithNext() {
    var isValidInput = true

    when (fieldName) {
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
        onNextAddress?.invoke(textField, city, state, postalCode)
      } else {
        onNext?.invoke(textField)
      }
    }
  }
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("RegisterScreen")) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()) {
              Text(
                  text = textShown,
                  style =
                      TextStyle(
                          fontSize = 20.sp,
                          lineHeight = 24.sp,
                          fontWeight = FontWeight(800),
                          color = Color(0xFF2490DF),
                          textAlign = TextAlign.Center,
                      ),
                  modifier = Modifier.testTag("EmailText"))

            var textVisible by remember {
                mutableStateOf(fieldName != "Password" && fieldName != "Confirm Password")
            }
            OutlinedTextField(
                value = textField,
                onValueChange = { textField = it },
                label = { Text(fieldName) },
                singleLine = true,
                keyboardOptions =
                (when (fieldName) {
                    "Password",
                    "Confirm Password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
                    "Phone Number" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                    else -> KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor =
                    Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray),
                visualTransformation =
                if (!textVisible) PasswordVisualTransformation() // Hide text if password
                else VisualTransformation.None,
                trailingIcon = {
                    when (fieldName) {
                        "Password",
                        "Confirm Password" -> {
                            val image =
                                if (textVisible) painterResource(id = R.drawable.baseline_visibility_24)
                                else painterResource(id = R.drawable.baseline_visibility_off_24)
                            val description = if (textVisible) "Hide password" else "Show password"
                            // Icon to toggle password visibility
                            IconButton(onClick = { textVisible = !textVisible }) {
                                Icon(painter = image, contentDescription = description)
                            }
                        }
                        else -> {}
                    }
                })

              Text(
                  text = errorText,
                  color = MaterialTheme.colorScheme.error,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(top = 4.dp).testTag("errorText"))

              if (isAddress) {

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("city") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("cityTag"),
                    colors =
                        OutlinedTextFieldDefaults.colors(
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
                    modifier = Modifier.fillMaxWidth().testTag("stateTag"),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                Color(0xFF2491DF), // Border color when the TextField is focused
                            focusedLabelColor =
                                Color(0xFF2491DF), // Label color when the TextField is focused
                            unfocusedBorderColor =
                                Color.Gray, // Additional customization for other states
                            unfocusedLabelColor = Color.Gray))

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("postalTag"),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                Color(0xFF2491DF), // Border color when the TextField is focused
                            focusedLabelColor =
                                Color(0xFF2491DF), // Label color when the TextField is focused
                            unfocusedBorderColor =
                                Color.Gray, // Additional customization for other states
                            unfocusedLabelColor = Color.Gray))
              }

              Spacer(modifier = Modifier.height(10.dp))

              Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        if (textField.isNotBlank()) {
                          Modifier.align(Alignment.Center).fillMaxWidth().padding(16.dp)
                        } else {
                          Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End) {
                      Button(
                          onClick = { proceedWithNext() },
                          modifier =
                              Modifier.wrapContentWidth()
                                  .testTag("arrowButton"), // Make the button wrap its content
                          colors =
                              ButtonDefaults.buttonColors( // Set the button's background color
                                  containerColor = Color(0xFF2491DF))) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Go forward",
                                tint = Color.White,
                                // Set the icon color to blue
                            )
                          }

                      Spacer(
                          modifier =
                              Modifier.height(16.dp)) // This adds space between the button and the
                      // progress
                      // bar

                      val progress = currentStep.toFloat() / 5
                      LinearProgressIndicator(
                          progress = { progress },
                          color = Color(0xFF2491DF),
                          modifier =
                              Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(10.dp)))
                    }
              }
            }
      }
}

fun isValidEmail(email: String): Boolean {
    // Valid email must contain characters followed by @,
    // followed by characters separated by a dot
    val regex = ".+@.+[.].+".toRegex()
    return email.matches(regex)
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    // Phone number must have at least 10 characters,
    // can start with a +, followed by only numbers
    // Can make it more precise
    val cond1 = phoneNumber.length >= 10
    val regex = "[+]?[0-9]+".toRegex()
    val cond2 = phoneNumber.matches(regex)
    return cond1 && cond2
}

fun isValidPassword(password: String): Boolean {
    // Password must have >=8 characters and include a number and letter
    val cond1 = password.length >= 8
    val cond2 = password.contains("[a-z]|[A-Z]".toRegex())
    val cond3 = password.contains("[0-9]".toRegex())
    return cond1 && cond2 && cond3
}

@Preview
@Composable
fun RegisterPreview() {
  val viewModel = remember { SignUpViewModel() } // In actual app, provide this via ViewModel

  val navController = rememberNavController()
  Register(viewModel, navController)
}
