package com.android.PetPamper.ui.screen

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.resources.C
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

private fun OnSignInResult(
    result: FirebaseAuthUIAuthenticationResult,
    updateUI: (Boolean, String) -> Unit
) {
  if (result.resultCode == ComponentActivity.RESULT_OK) {
    // Successfully signed in
    val user = FirebaseAuth.getInstance().currentUser
    updateUI(true, user?.email ?: "Unknown")
  } else {
    updateUI(false, "")
  }
}

@Composable
fun SignIn(navController: NavHostController) {
    var isGroomer by remember { mutableStateOf(false) }
    val db = Firebase.firestore

  var signedIn by remember { mutableStateOf(false) }
  var GoogleEmail by remember { mutableStateOf("") }

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var firebaseConnection = FirebaseConnection()
  var login by remember { mutableStateOf(true) }

    var errorMessage by remember {
        mutableStateOf("Login failed, email or password is incorrect")
    }

  val signInLauncher =
      rememberLauncherForActivityResult(
          FirebaseAuthUIActivityResultContract(),
      ) { res ->
        OnSignInResult(res) { success, email ->
          signedIn = success
          GoogleEmail = email
        }
      }

  val providers =
      arrayListOf(
          AuthUI.IdpConfig.GoogleBuilder().build(),
      )

  if (!signedIn) {

    Surface(
        modifier =
        Modifier
            .fillMaxSize()
            .testTag("LoginScreen")
            .verticalScroll(rememberScrollState())) {
          Column(horizontalAlignment = Alignment.End) {
            Image(
                painter = painterResource(id = R.mipmap.dog_rounded_foreground),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape))
          }

          Spacer(modifier = Modifier.height(5.dp))

          Column(
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Welcome,",
                    style =
                        TextStyle(
                            fontSize = 40.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight(800),
                            color = Color(0xFF2490DF),
                        ),
                    modifier = Modifier.testTag("LoginTitle"))
                Text(
                    text = "Login to start with PetPamper 👋",
                    style =
                        TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 34.sp,
                            fontWeight = FontWeight(800),
                            color = Color.Black,
                        ))

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email, // Bind to state in real implementation
                    onValueChange = { email = it }, // Implement logic in real implementation
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password, // Bind to state in real implementation
                    onValueChange = { password = it }, // Implement logic in real implementation
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
                      modifier = Modifier
                          .fillMaxWidth()
                          .testTag("ErrorMessage"))

                  Spacer(modifier = Modifier.height(4.dp))
                }

                CustomTextButton(
                    "Forgot password?",
                    "",
                    "forgetButton",
                    { navController.navigate("EmailScreen") })

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        errorMessage = "Login failed, email or password is incorrect"
                      if (email.isBlank() || password.isBlank()) {
                        login = false
                      } else {
                        firebaseConnection.loginUser(
                            email,
                            password,
                            {
                                if (!isGroomer) {
                                    login = true
                                    navController.navigate("HomeScreen/${email}")
                                }
                                else {
                                    val groomerRef = db.collection("groomers")
                                        .document(email)
                                    groomerRef.get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                login = true
                                                Log.d("Firebase query", "Groomer found," +
                                                        " name is ${document.get("name")}")
                                            }
                                            else {
                                                login = false
                                                errorMessage = "User is not registered as a groomer"
                                                Log.e("Firebase query", "No such groomer")
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            login = false
                                            Log.e("Firebase query", "Get failed with ",
                                                exception)
                                        }
                                }
                            },
                            { login = false })
                      }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF2491DF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("LoginButton")) {
                      Text("LOG IN", fontSize = 18.sp)
                    }

                Spacer(modifier = Modifier.height(15.dp))

                CustomTextButton("REGISTER", "Don't have an account? ", "registerButton") {
                  if (!isGroomer) navController.navigate("RegisterScreen1")
                  else navController.navigate("GroomerRegisterScreen")
                }

                Spacer(modifier = Modifier.height(20.dp))

              if (!isGroomer)
              {
                  Row(
                      horizontalArrangement = Arrangement.Center,
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.fillMaxWidth()) {
                      Text(
                          text = "or sign in with",
                          style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 22.sp,
                              fontWeight = FontWeight(800),
                              color = Color(0xFF52525B),
                              textAlign = TextAlign.Center,
                          ))
                  }

                  Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center,
                      modifier =
                      Modifier
                          .height(80.dp)
                          .fillMaxWidth() // This will make the Column fill the entire screen
                  ) {
                      Image(
                          painter = painterResource(id = R.mipmap.google_logo_rounded_foreground),
                          contentDescription = "Google Logo",
                          modifier =
                          Modifier
                              .size(80.dp) // Size of the image
                              .clip(CircleShape) // Clip image to circle shape
                              .clickable {
                                  val signInIntent =
                                      AuthUI
                                          .getInstance()
                                          .createSignInIntentBuilder()
                                          .setAvailableProviders(providers)
                                          .setIsSmartLockEnabled(false)
                                          .build()
                                  signInLauncher.launch(signInIntent)
                              }
                              .testTag("googleSignInButton"))
                  } // Define this composable to match the style

                  Spacer(modifier = Modifier.height(16.dp))
              }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Switch(
                        checked = isGroomer,
                        onCheckedChange = {
                            isGroomer = it
                        },
                        modifier = Modifier.offset(x=100.dp)
                    )
                    Text(
                        text = if (isGroomer) "I am a groomer" else "I am a user",
                        style = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp, fontWeight = FontWeight(600)
                        ),
                        modifier = Modifier.offset(x=110.dp)
                    )
//                      Button(
//                          onClick = { navController.navigate("GroomerRegisterScreen") },
//                          colors = ButtonDefaults.buttonColors(Color.Black),
//                          modifier = Modifier
//                              .width(200.dp)
//                              .height(48.dp)) {
//                            Text("I am a Groomer", color = Color.White, fontSize = 16.sp)
//                          }
                    }
              }
        }
  } else {
    firebaseConnection.getUserUidByEmail(GoogleEmail).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Check if the query found any documents
        val documents = task.result?.documents
        if (documents != null && documents.isNotEmpty()) {
          // If documents are found, it means there is a user ID associated with the email
          // Navigate to the home screen
          navController.navigate("HomeScreen/$GoogleEmail")
        } else {
          // If no documents are found, it means no user ID is associated with the email
          // Navigate to the register screen
          navController.navigate("RegisterScreenGoogle/$GoogleEmail")
        }
      } else {
        // If the task itself failed (e.g., due to network issues), you may want to handle this case
        // as well.
        // For example, you might want to show an error message or try again.
        // Here, we'll just log the error.
        Log.e("FirebaseQuery", "Error querying user by email: ${task.exception}")
        // Optionally navigate to an error screen or show an error message
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}

@Composable
fun CustomTextButton(
    tag: String,
    annotated: String = "",
    testTag: String,
    onRegisterClick: () -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        Text(
            text = annotated,
            style =
                TextStyle(
                    textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight(600)))

        ClickableText(
            text = AnnotatedString(text = tag),
            style =
                TextStyle(
                    color = Color(0xFF2491DF), fontSize = 16.sp, fontWeight = FontWeight(600)),
            modifier = Modifier.testTag(testTag),
            onClick = { onRegisterClick() })
      }
}

@Preview
@Composable
fun PreviewRegisterScreen() {
  val navController = rememberNavController()
  CustomTextButton("REGISTER", "Don't have an account? ", "registerButton") {}
}
