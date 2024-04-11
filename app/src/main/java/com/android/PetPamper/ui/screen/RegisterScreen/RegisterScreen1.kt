package com.android.PetPamper.ui.screen.RegisterScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

  when (currentStep) {
    1 ->
        SignUpScreenLayout("Let’s start with your name" , "Name", viewModel) { newName ->
          viewModel.name = newName
          currentStep++
        }
    2 ->
        SignUpScreenLayout("Hello ${viewModel.name}, enter your email" , "Email", viewModel) { newEmail ->
          viewModel.email = newEmail
          currentStep++
        }

      3 ->
          SignUpScreenLayout("What’s your phone number?" , "Phone Number", viewModel) { newPhoneNumber ->
              viewModel.phoneNumber = newPhoneNumber
              currentStep++
          }

      4 ->
          SignUpScreenLayout1() { street, city, state, postalcode ->
              viewModel.address.city = city
              viewModel.address.state = state
              viewModel.address.street = street
              viewModel.address.postalCode = postalcode
              currentStep++
          }

      5 ->
          SignUpScreenLayout("Great! Create your password" , "Password", viewModel) { password ->
              viewModel.password = password
              val firebaseConnection = FirebaseConnection()

              firebaseConnection.registerUser(
                  viewModel.email,
                  viewModel.password,
                  onSuccess = { },
                  onFailure = { Exception("Registration failed") }
              )

              firebaseConnection.addUser(
                  User(viewModel.name, viewModel.email, viewModel.phoneNumber, viewModel.address),
                  onSuccess = { currentStep++ },
                  onFailure = { })
          }

      6 ->
          navController.navigate("LoginScreen")
          }





  // Add more steps as needed
  }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenLayout(textShown: String, fieldname: String, viewModel: SignUpViewModel, onNext: (String) -> Unit){
    val keyboardController = LocalSoftwareKeyboardController.current
    var textField by remember { mutableStateOf("") }

    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
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

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = textField,
                onValueChange = { textField = it },
                label = { Text(fieldname) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { onNext(textField) },
                    modifier = Modifier.wrapContentWidth(), // Make the button wrap its content
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
                        16.dp)) // This adds space between the button and the progress bar

                LinearProgressIndicator(
                    progress = 0.2f,
                    color = Color(0xFF2491DF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenLayout1(onNext: (String, String, String, String) -> Unit){
    val keyboardController = LocalSoftwareKeyboardController.current
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var postaleCode by remember { mutableStateOf("") }

    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Street",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Street") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "City",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("city") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "State",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "Postal Code",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = postaleCode,
                onValueChange = { postaleCode = it },
                label = { Text("Postal Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { onNext(street, city, state, postaleCode) },
                    modifier = Modifier.wrapContentWidth(), // Make the button wrap its content
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
                        16.dp)) // This adds space between the button and the progress bar

                LinearProgressIndicator(
                    progress = 0.2f,
                    color = Color(0xFF2491DF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)))
            }
        }
    }


}
@Preview
@Composable
fun SignUpScreenPreview() {
  val viewModel = remember { SignUpViewModel() } // In actual app, provide this via ViewModel
    val navController  = rememberNavController()
    SignUpScreen(viewModel, navController)
}
