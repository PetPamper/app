package com.android.PetPamper.ui.screen.register

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import com.android.PetPamper.R
import com.android.PetPamper.StringSanitizer
import com.android.PetPamper.connectUser
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.GroomerReviews
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.ui.screen.users.CustomTextButton
import com.android.PetPamper.viewmodel.AddressViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User

class GroomerSignUpViewModel {

  var name by mutableStateOf("")
  var email by mutableStateOf("")
  var phoneNumber by mutableStateOf("")
  var password by mutableStateOf("")
  var address by mutableStateOf(Address("", "", "", "", LocationMap()))
  var experienceYears by mutableStateOf("")
  var groomerServices = mutableStateListOf<String>()
  var petTypes = mutableStateListOf<String>()
  var profilePicture by mutableStateOf("")
  var price by mutableIntStateOf(0)
  var locationMap by mutableStateOf(LocationMap())
}

const val NUM_STEPS = 12

@Composable
fun GroomerRegister(
    viewModel: GroomerSignUpViewModel,
    navController: NavController,
    hasUserAccount: Boolean = false
) {
  val firebaseConnection = FirebaseConnection.getInstance()
  val db = Firebase.firestore

  val initialStep = if (!hasUserAccount) 1 else 20

  var currentStep by remember { mutableIntStateOf(initialStep) }

  var registeredAsUser by remember { mutableStateOf(false) }

  when (currentStep) {
    1 -> {
      GroomerRegisterLayout(
          false,
          1,
          "Let’s start with your name",
          "Name",
          isValidInput = ::isValidName,
          errorText = "Please enter a valid name.",
          onNext = { newName ->
            viewModel.name = newName
            currentStep++
          })
    }
    2 ->
        GroomerRegisterLayout(
            true,
            2,
            "Hello ${viewModel.name}, enter your email",
            "Email",
            isValidEmail = ::isValidEmail1,
            errorText = "Please enter a valid email.",
            onNext = { newEmail ->
              viewModel.email = StringSanitizer.sanitizeEmail(newEmail)
              currentStep++
            })
    3 ->
        GroomerRegisterLayout(
            false,
            3,
            "What’s your phone number?",
            "Phone Number",
            isValidInput = ::isValidPhone,
            errorText = "Please enter a valid phone number.",
            onNext = { newPhoneNumber ->
              viewModel.phoneNumber = StringSanitizer.sanitizePhone(newPhoneNumber)
              currentStep++
            })
    4 ->
        GroomerRegisterLayout(
            false,
            4,
            "Great! Create your password",
            "Password",
            isValidInput = ::isValidPassword,
            errorText = "Password must be at least 8 characters.",
            onNext = { password ->
              viewModel.password = password
              currentStep++
            })
    5 -> {
      GroomerRegisterLayout(
          false,
          5,
          "Confirm your password",
          "Confirm Password",
          isValidInput = { confirmedPassword -> confirmedPassword == viewModel.password },
          errorText = "Passwords do not match.",
          onNext = { _ -> currentStep++ })
    }
    6 ->
        GroomerRegisterMultipleLayout(
            viewModel,
            6,
            "Enter your address",
            listOf("Street", "City", "State", "Postal Code"),
            onNext = { fieldsList ->
              viewModel.address.city = fieldsList[0]
              viewModel.address.state = fieldsList[1]
              viewModel.address.street = fieldsList[2]
              viewModel.address.postalCode = fieldsList[3]
              viewModel.address.location = viewModel.locationMap
              currentStep++
            })
    7 ->
        GroomerRegisterLayout(
            false,
            7,
            "How many years of experience do you have as a groomer?",
            "Experience Years",
            onNext = { experienceYears ->
              viewModel.experienceYears = StringSanitizer.sanitizeFloat(experienceYears)
              currentStep++
            })
    8 ->
        GroomerRegisterLayout(
            false,
            8,
            "What is your average service price for an hour",
            "Price",
            onNext = { price ->
              viewModel.price = StringSanitizer.sanitizeInt(price).toInt()
              currentStep++
            })
    9 ->
        GroomerRegisterCheckboxLayout(
            9,
            "What types of services do you provide?",
            listOf(
                "Bath",
                "Brushing",
                "Eye/ear cleaning",
                "Hair trimming",
                "Nail trimming",
                "Teeth brushing",
                "De-shedding",
                "Dematting"),
            onNext = { groomerServices ->
              for (service in groomerServices) {
                viewModel.groomerServices.add(service)
              }
              currentStep++
            })
    10 -> {

      GroomerRegisterCheckboxLayout(
          10,
          "What types of pets do you groom?",
          listOf(
              "Dog", "Cat", "Bird", "Rabbit", "Hamster", "Guinea Pig", "Ferret", "Reptile", "Fish"),
          onNext = { petTypes ->
            for (petType in petTypes) {
              viewModel.petTypes.add(petType)
            }
            currentStep++
          })
    }
    11 -> {
      GroomerProfilePicture(viewModel) { proceed ->
        if (proceed) {
          currentStep++
        }
      }
    }
    12 -> {
      if (!registeredAsUser) {
        firebaseConnection.registerUser(
            viewModel.email,
            viewModel.password,
            onSuccess = {
              Log.d("SignUp1", "Registering user")
              firebaseConnection.addGroomer(
                  Groomer(
                      viewModel.name,
                      viewModel.email,
                      viewModel.phoneNumber,
                      viewModel.address,
                      viewModel.experienceYears,
                      viewModel.groomerServices,
                      viewModel.petTypes,
                      viewModel.profilePicture,
                      viewModel.price),
                  onSuccess = {
                    connectUser(
                        ChatClient.instance(),
                        User(viewModel.email, viewModel.name, viewModel.profilePicture),
                        onSuccess = { navController.navigate("LoginScreen") },
                        onError = { error -> Log.e("SignUp", "Chat connection failed") })

                    firebaseConnection.addGroomerReview(
                        GroomerReviews(viewModel.email, 5.0, 0),
                        onSuccess = { navController.navigate("LoginScreen") },
                        onFailure = { error -> Log.e("SignUp", "Review failed", error) })
                  },
                  onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
            },
            onFailure = { error ->
              Log.e("SignUp", "Registration failed", error)
              Log.d("SignUp1", "Registering user")
            })
      } else {
        // Need to check that groomer wasn't already registered to avoid duplicate accounts
        val groomerRef = db.collection("groomers").document(viewModel.email)
        groomerRef
            .get()
            .addOnSuccessListener { document ->
              if (!document.exists()) {
                firebaseConnection.addGroomer(
                    Groomer(
                        viewModel.name,
                        viewModel.email,
                        viewModel.phoneNumber,
                        viewModel.address,
                        viewModel.experienceYears,
                        viewModel.groomerServices,
                        viewModel.petTypes,
                        viewModel.profilePicture,
                        viewModel.price),
                    onSuccess = {
                      firebaseConnection.addGroomerReview(
                          GroomerReviews(viewModel.email, 5.0, 0),
                          onSuccess = { navController.navigate("LoginScreen") },
                          onFailure = { error -> Log.e("SignUp", "Review failed", error) })
                    },
                    onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
              } else {
                Log.e("AlreadyRegistered", "user was already registered as groomer")
              }
            }
            .addOnFailureListener { exception ->
              Log.e("Firebase query", "Get failed with ", exception)
            }
      }
    }
    20 -> {
      var email by remember { mutableStateOf("") }
      var password by remember { mutableStateOf("") }
      var login by remember { mutableStateOf(true) }

      var errorMessage by remember {
        mutableStateOf("Login failed, email or password is incorrect")
      }

      Column(
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Please enter your user credentials",
                style =
                    TextStyle(
                        fontSize = 23.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF2490DF),
                        textAlign = TextAlign.Center,
                    ),
                modifier = Modifier.testTag("AlreadyUserText"))

            OutlinedTextField(
                value = email,
                onValueChange = { email = StringSanitizer.sanitizeEmail(it) },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(15.dp))

            if (!login) {
              Text(
                  text = errorMessage,
                  color = Color.Red,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth().testTag("errorMessage"))

              Spacer(modifier = Modifier.height(4.dp))
            }

            CustomTextButton("Forgot password?", "", "forgetButton") {
              navController.navigate("EmailScreen")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                  errorMessage = ""
                  if (email.isBlank() || password.isBlank()) {
                    login = false
                  } else {
                    firebaseConnection.loginUser(
                        email,
                        password,
                        {
                          val userRef = db.collection("users").document(email)
                          userRef
                              .get()
                              .addOnSuccessListener { document ->
                                if (document.exists()) {
                                  login = true
                                  Log.d(
                                      "Firebase query",
                                      "User found," + " name is ${document.get("name")}")
                                  viewModel.name = document.get("name").toString()
                                  viewModel.email = document.get("email").toString()
                                  viewModel.phoneNumber = document.get("phoneNumber").toString()
                                  registeredAsUser = true
                                  currentStep = 6
                                } else {
                                  login = false
                                  errorMessage = "User is not registered"
                                  Log.e("Firebase query", "No such user")
                                }
                              }
                              .addOnFailureListener { exception ->
                                login = false
                                errorMessage = "Login failed, email or password is incorrect"
                                Log.e("Firebase query", "Get failed with ", exception)
                              }
                        },
                        { login = false })
                  }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF2491DF)),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("LoginButton")) {
                  Text("LOG IN", fontSize = 18.sp)
                }
          }
    }
  }

  // Add more steps as needed
}

@Composable
fun GroomerProfilePicture(viewModel: GroomerSignUpViewModel, onNext: ((Boolean) -> Unit)?) {
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var image by remember { mutableStateOf("") }
  val context = LocalContext.current

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("GroomerRegisterScreen")) {
        Text(
            text = "Upload a profile picture",
            style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ),
            modifier = Modifier.testTag("DisplayText"))

        Spacer(modifier = Modifier.height(16.dp))

        GalleryImagePicker { uri ->
          if (uri != null) {
            // Get a reference to the storage service
            val storageRef = FirebaseStorage.getInstance().reference

            val fileRef = storageRef.child("images/${uri.lastPathSegment}")
            val uploadTask = fileRef.putFile(uri)

            uploadTask
                .addOnSuccessListener { taskSnapshot ->
                  taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri
                    ->
                    imageUri = downloadUri // Store download URI instead of local URI
                    viewModel.profilePicture = downloadUri.toString()
                  }
                }
                .addOnFailureListener {
                  // Handle unsuccessful uploads
                  Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
          } else {
            imageUri = null
            viewModel.profilePicture = imageUri.toString()
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { onNext?.invoke(true) },
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
                    modifier = Modifier.height(16.dp)) // This adds space between the button and the
                // progress bar

                val progress = 9f / NUM_STEPS
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF2491DF),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(10.dp)))
              }
        }
      }
}

@Composable
fun GroomerRegisterLayout(
    isEmail: Boolean,
    currentStep: Int,
    textShown: String,
    fieldName: String,
    isValidInput: ((String) -> Boolean)? = null,
    isValidEmail: ((String) -> Pair<Boolean, Boolean>)? = null,
    errorText: String = "",
    onNext: ((String) -> Unit)? = null
) {

  var textField by remember { mutableStateOf("") }
  var shownErrorText by remember { mutableStateOf("") }

  fun proceedWithNext() {
    var proceed = true

    if (isValidInput?.invoke(textField) == false) {
      shownErrorText = errorText
      proceed = false
    }

    if (isValidEmail?.invoke(textField)?.first == false) {
      proceed = false
      shownErrorText = errorText
    }

    if (proceed) {
      shownErrorText = ""
      onNext?.invoke(textField)
    }
  }

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("GroomerRegisterScreen")) {
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
            modifier = Modifier.testTag("DisplayText"))

        var textVisible by remember {
          mutableStateOf(fieldName != "Password" && fieldName != "Confirm Password")
        }
        OutlinedTextField(
            value = textField,
            onValueChange = { textField = it },
            label = { Text(fieldName) },
            singleLine = true,
            keyboardOptions =
                when (fieldName) {
                  "Password",
                  "Confirm Password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
                  "Phone Number" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                  else -> KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                },
            visualTransformation =
                if (!textVisible) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().testTag("InputText"),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =
                        Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                        Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray),
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
            text = shownErrorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp).testTag("errorText"))

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
                  if (isKeyboardOpen().value) {
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
                    modifier = Modifier.height(16.dp)) // This adds space between the button and the
                // progress bar

                val progress = currentStep.toFloat() / NUM_STEPS
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF2491DF),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(10.dp)))
              }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroomerRegisterMultipleLayout(
    viewModel: GroomerSignUpViewModel,
    currentStep: Int,
    textShown: String,
    fieldNames: List<String>,
    areValidInputs: ((List<String>) -> (List<Boolean>))? = null,
    errorTexts: List<String>? = null,
    onNext: ((List<String>) -> Unit)? = null,
) {
  val numFields = fieldNames.size
  val textFields = remember { mutableStateListOf<String>() }
  val shownErrorTexts = remember { mutableStateListOf<String>() }
  val addressVM = AddressViewModel()
  var expandedState by remember { mutableStateOf(false) }
  val locationOptions = remember { mutableStateListOf<Address>() }
  val focusRequester = remember { FocusRequester() }

  for (i in 1..numFields) {
    textFields.add("")
    shownErrorTexts.add("")
  }

  fun proceedWithNext() {
    var isValidInput = true
    val inputsValid = areValidInputs?.invoke(textFields)

    for (i in 0 until numFields) {
      if (inputsValid?.get(i) == false) {
        isValidInput = false
        shownErrorTexts[i] = errorTexts?.get(i) ?: ""
      }
    }

    if (isValidInput) {
      for (i in 0 until numFields) {
        shownErrorTexts[i] = ""
      }
      onNext?.invoke(textFields)
    }
  }
  LazyColumn(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("GroomerRegisterScreen")) {
        item {
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
              modifier = Modifier.testTag("DisplayText"))
        }
        itemsIndexed(fieldNames) { i, _ ->
          if (i == 0) {
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
                      value = textFields[0],
                      onValueChange = { newValue ->
                        textFields[0] = newValue
                        addressVM.fetchAddresses(newValue) { addresses ->
                          if (addresses != null) {
                            locationOptions.clear()
                            locationOptions.addAll(addresses)
                            Log.d(
                                "LocationInput",
                                "Updated location options: ${locationOptions.joinToString { it.location.name }}")
                          }
                        }
                      },
                      label = { Text("Location") },
                      placeholder = { Text("Enter an address") },
                      modifier =
                          Modifier.fillMaxWidth()
                              .menuAnchor()
                              .focusRequester(focusRequester)
                              .testTag("InputText$i"),
                      trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState)
                      },
                  )
                  DropdownMenu(
                      expanded = expandedState, onDismissRequest = { expandedState = false }) {
                        locationOptions.forEach { location ->
                          DropdownMenuItem(
                              onClick = {
                                textFields[0] = location.location.name
                                viewModel.locationMap = location.location
                                expandedState = false
                              }) {
                                Text(location.location.name)
                              }
                        }
                      }
                }
            Spacer(modifier = Modifier.height(10.dp))
          } else {

            OutlinedTextField(
                value = textFields[i],
                onValueChange = { textFields[i] = it },
                label = { Text(fieldNames[i]) },
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                modifier = Modifier.fillMaxWidth().testTag("InputText$i"),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor =
                            Color(0xFF2491DF), // Border color when the TextField is focused
                        focusedLabelColor =
                            Color(0xFF2491DF), // Label color when the TextField is focused
                        unfocusedBorderColor =
                            Color.Gray, // Additional customization for other states
                        unfocusedLabelColor = Color.Gray))

            Text(
                text = shownErrorTexts[i],
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp).testTag("errorText$i"))
          }
        }

        item {
          Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    if (isKeyboardOpen().value) {
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
                  // progress bar

                  val progress = currentStep.toFloat() / NUM_STEPS
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

@Composable
fun GroomerRegisterCheckboxLayout(
    currentStep: Int,
    textShown: String,
    checkboxOptions: List<String>,
    onNext: ((List<String>) -> Unit)? = null
) {
  val numOptions = checkboxOptions.size

  val boxesChecked = remember { mutableStateListOf<Boolean>() }
  val checkedOptions = remember { mutableStateListOf<String>() }

  for (i in 0 until numOptions) {
    boxesChecked.add(false)
  }
  LazyColumn(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("GroomerRegisterScreen")) {
        item {
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
              modifier = Modifier.testTag("DisplayText"))

          Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(checkboxOptions) { i, _ ->
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = boxesChecked[i],
                onCheckedChange = { isChecked -> boxesChecked[i] = isChecked },
                modifier = Modifier.testTag("Checkbox$i"))
            Text(text = checkboxOptions[i])
          }

          Spacer(modifier = Modifier.height(10.dp))
        }

        item {
          Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    if (isKeyboardOpen().value) {
                      Modifier.fillMaxWidth().padding(bottom = 50.dp, start = 16.dp)
                    } else {
                      Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End) {
                  Button(
                      onClick = {
                        checkedOptions.clear()
                        for (i in 0 until numOptions) {
                          if (boxesChecked[i]) {
                            checkedOptions.add(checkboxOptions[i])
                          }
                        }
                        onNext?.invoke(checkedOptions)
                      },
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
                  // progress bar

                  val progress = currentStep.toFloat() / NUM_STEPS
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

@Composable
fun GalleryImagePicker(onImagePicked: (Uri?) -> Unit) {
  val context = LocalContext.current
  var imageUri by remember { mutableStateOf<Uri?>(null) }

  // Remember a launcher for picking an image from the gallery
  val galleryLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri: Uri? ->
            imageUri = uri
            onImagePicked(uri)
          })

  Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Select Image from Gallery") }

  imageUri?.let {
    // Here you can use the URI to display the image or process it further
    // Displaying a preview is often useful
    ImagePreview(uri = it)
  }
}

@Composable
fun ImagePreview(uri: Uri) {
  // Using Accompanist's Coil to load and display an image from the URI
  val painter = rememberAsyncImagePainter(model = uri)
  Image(painter = painter, contentDescription = "Selected Image")
}

@Preview
@Composable
fun GroomerRegisterPreview() {
  val viewModel = remember { GroomerSignUpViewModel() }
  val navController = rememberNavController()
  GroomerRegister(viewModel, navController)
}
