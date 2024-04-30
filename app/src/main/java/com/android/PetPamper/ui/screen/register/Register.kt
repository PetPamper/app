package com.android.PetPamper.ui.screen.register

import LocationViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.User

class SignUpViewModel {

  var name by mutableStateOf("")
  var email by mutableStateOf("")
  var phoneNumber by mutableStateOf("")
  var address by mutableStateOf(Address("", "", "", "", LocationMap()))
  var password by mutableStateOf("")
  var locationMap: LocationMap = LocationMap()
}

@Composable
fun Register(currentStep1: Int, viewModel: SignUpViewModel, navController: NavController) {
  var currentStep by remember { mutableIntStateOf(currentStep1) }

  when (currentStep) {
    1 ->
        RegisterLayout(
            viewModel = viewModel,
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
            viewModel,
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
            viewModel,
            3,
            false,
            "What’s your phone number?",
            "Phone",
            onNext = { newPhoneNumber ->
              viewModel.phoneNumber = newPhoneNumber
              currentStep++
            })
    4 ->
        RegisterLayout(
            viewModel,
            4,
            true,
            "Enter your Address?",
            "Street",
            onNextAddress = { street, city, state, postalCode ->
              viewModel.address.street = street
              viewModel.address.city = city
              viewModel.address.state = state
              viewModel.address.postalCode = postalCode
              viewModel.address.location.latitude = viewModel.locationMap.latitude
              viewModel.address.location.longitude = viewModel.locationMap.longitude
              viewModel.address.location.name = viewModel.locationMap.name
              currentStep++
            })
    5 ->
        RegisterLayout(
            viewModel,
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
          viewModel,
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
    7 -> {
      BoxWithConstraints(
          modifier = Modifier.fillMaxSize().padding(16.dp).testTag("ForgetPassword")) {
            val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()) {
                  Text(
                      text = "Congratulation! You successfully created an account.",
                      style =
                          TextStyle(
                              fontSize = 20.sp,
                              lineHeight = 24.sp,
                              fontWeight = FontWeight(800),
                              color = Color(0xFF2490DF),
                              textAlign = TextAlign.Center,
                          ),
                      modifier = Modifier.testTag("SuccessfullMessage"))
                  Spacer(modifier = Modifier.height(20.dp))
                  Spacer(modifier = Modifier.height(10.dp))
                  Image(
                      painter = painterResource(id = R.drawable.check_success),
                      contentDescription = "Succuss Icon",
                      modifier = Modifier.size(120.dp).clip(CircleShape))
                  Column(
                      modifier = Modifier.fillMaxSize().padding(16.dp),
                      verticalArrangement = Arrangement.Bottom,
                      horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = { navController.navigate("LoginScreen") },
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
                      }
                }
          }
    }

  // Add more steps as needed
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterLayout(
    viewModel: SignUpViewModel,
    currentStep: Int,
    isAddress: Boolean,
    textShown: String = "Let’s start with your name",
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
  var locationViewModel = LocationViewModel()
  var expandedState by remember { mutableStateOf(false) }
  val locationOptions = remember { mutableStateListOf<LocationMap>() }
  val focusRequester = remember { FocusRequester() }

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
      "Phone" ->
          if (!isValidPhone(textField)) {
              errorText = "Please enter a valid phone number."
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
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go back",
                        modifier = Modifier.clickable {}.size(30.dp),
                        tint = Color.Black)

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = textShown,
                        style =
                            TextStyle(
                                fontSize = 23.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(800),
                                color = Color(0xFF2490DF),
                                textAlign = TextAlign.Center,
                            ),
                        modifier = Modifier.testTag("inputLabel"))
                  }

              Spacer(modifier = Modifier.height(10.dp))

              if (!isAddress) {

                OutlinedTextField(
                    value = textField,
                    onValueChange = { textField = it },
                    label = { Text(fieldName) },
                    singleLine = true,
                    visualTransformation =
                        if (fieldName == "Password" || fieldName == "Confirm Password")
                            PasswordVisualTransformation()
                        else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth().testTag("inputText"),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                Color(0xFF2491DF), // Border color when the TextField is focused
                            focusedLabelColor =
                                Color(0xFF2491DF), // Label color when the TextField is focused
                            unfocusedBorderColor =
                                Color.Gray, // Additional customization for other states
                            unfocusedLabelColor = Color.Gray))
              } else {
                ExposedDropdownMenuBox(
                    expanded = expandedState && locationOptions.isNotEmpty(),
                    onExpandedChange = {
                      expandedState = it
                      if (expandedState) {
                        // Request focus again if the dropdown expands
                        focusRequester.requestFocus()
                      }
                    },
                    modifier = Modifier.fillMaxWidth()) {
                      OutlinedTextField(
                          value = textField,
                          onValueChange = { newValue ->
                            textField = newValue
                            locationViewModel.fetchLocation(newValue) { locations ->
                              if (locations != null) {
                                locationOptions.clear()
                                locationOptions.addAll(locations)
                                Log.d(
                                    "LocationInput",
                                    "Updated location options: ${locationOptions.joinToString { it.name }}")
                              }
                            }
                          },
                          label = { Text("Location") },
                          placeholder = { Text("Enter an address") },
                          modifier =
                              Modifier.fillMaxWidth()
                                  .menuAnchor()
                                  .focusRequester(focusRequester)
                                  .testTag("inputText"),
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState)
                          },
                      )
                      DropdownMenu(
                          expanded = expandedState, onDismissRequest = { expandedState = false }) {
                            locationOptions.forEach { location ->
                              DropdownMenuItem(
                                  onClick = {
                                    textField = location.name
                                    viewModel.locationMap = location
                                    expandedState = false
                                  }) {
                                    Text(location.name)
                                  }
                            }
                          }
                    }
              }

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
                          Modifier.fillMaxWidth().padding(bottom = 50.dp, start = 16.dp)
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

                      val progress = currentStep.toFloat() / 7
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

fun isValidName(name: String) = name.isNotBlank() // Add more conditions as necessary

fun isValidEmail(email: String) =
    email.contains('@') && email.contains('.') // Simplified validation

fun isValidPhone(phone: String): Boolean {
    var _phone = phone.replace(Regex("-|\\s"), "")
    if (_phone.startsWith("+")) {
        _phone = _phone.replaceFirst("+","00")
    }
    return _phone.matches(Regex("\\d*")) && phone.isNotBlank()
}

fun isValidPassword(password: String) = password.length >= 8 // Basic condition for demonstration

@Preview
@Composable
fun RegisterPreview() {
  val viewModel = remember { SignUpViewModel() } // In actual app, provide this via ViewModel

  val navController = rememberNavController()
  Register(1, viewModel, navController)
}
