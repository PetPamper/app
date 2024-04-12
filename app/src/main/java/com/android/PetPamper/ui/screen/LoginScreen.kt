package com.android.PetPamper.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.resources.C
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

private fun OnSignInResult(
    result: FirebaseAuthUIAuthenticationResult,
    updateUI: (Boolean, String) -> Unit
) {
    if (result.resultCode == ComponentActivity.RESULT_OK) {
        // Successfully signed in
        val user = FirebaseAuth.getInstance().currentUser
        updateUI(true, user?.displayName ?: "Unknown")
    } else {
        updateUI(false, "")
    }
}

@Composable
fun SignIn(navController: NavHostController) {
    var signedIn by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firebaseConnection = FirebaseConnection()
    var login by remember { mutableStateOf(true) }

    val signInLauncher =
        rememberLauncherForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
            OnSignInResult(res) { success, name ->
                signedIn = success
                displayName = name
            }
        }

    val providers =
        arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

    if (!signedIn) {

        Surface(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column(horizontalAlignment = Alignment.End) {
                Image(
                    painter = painterResource(id = R.mipmap.dog_rounded_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp).clip(CircleShape))
            }

            Spacer(modifier = Modifier.height(5.dp))

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Welcome,",
                    style =
                    TextStyle(
                        fontSize = 40.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF2490DF),
                    ))
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

                CustomTextButton("Forgot password?") { navController.navigate("EmailScreen") }

                /*Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        if (!login) {
                            Text(
                                text = "Login failed, email or password is incorrect",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Forgot Password?",
                            style =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(800),
                                color = Color(0xFF2490DF),
                                textAlign = TextAlign.Center,
                                )
                        )
                }*/

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            login = false
                        } else {
                            firebaseConnection.loginUser(
                                email,
                                password,
                                {
                                    login = true
                                    navController.navigate("HomeScreen/${email}")
                                },
                                { login = false })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF2491DF)),
                    modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Text("LOG IN", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(15.dp))

                CustomTextButton("REGISTER", "Don't have an account? ") {
                    navController.navigate("RegisterScreen1")
                }

                Spacer(modifier = Modifier.height(20.dp))

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

                GoogleSignInButton() // Define this composable to match the style

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /* Implement register as a groomer logic */},
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.width(200.dp).height(48.dp)) {
                        Text("I am a Groomer", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    } else {
        Greeting(name = "PetPamper")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}

@Composable
fun GoogleSignInButton() {
    // Create a button that looks like the Google Sign-In button
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
        Modifier.height(80.dp).fillMaxWidth() // This will make the Column fill the entire screen
    ) {
        Image(
            painter = painterResource(id = R.mipmap.google_logo_rounded_foreground),
            contentDescription = "Google Logo",
            modifier =
            Modifier.size(80.dp) // Size of the image
                .clip(CircleShape) // Clip image to circle shape
                .clickable {
                    // TODO: Implement Google Sign-In logic
                })
    }
}

@Composable
fun CustomTextButton(tag: String, annotated: String = "", onRegisterClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        val annotatedString = buildAnnotatedString {
            append(annotated)
            // Attach an annotation tag to the "REGISTER" text
            pushStringAnnotation(tag = tag, annotation = tag.lowercase())

            withStyle(
                style =
                SpanStyle(
                    color = Color(0xFF2491DF), fontSize = 16.sp, fontWeight = FontWeight(600))) {
                append(tag)
            }
            pop() // This call removes the last added style and/or annotation from the stack
        }

        ClickableText(
            text = annotatedString,
            style =
            TextStyle(textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight(600)),
            onClick = { offset ->
                // Check if the click happened on the "REGISTER" text
                annotatedString
                    .getStringAnnotations(tag = tag, start = offset, end = offset)
                    .firstOrNull()
                    ?.let { onRegisterClick() }
            },
            modifier = Modifier.padding(horizontal = 16.dp) // Optional: for better text wrapping
        )
    }
}

// Button(
// onClick = {
//    val signInIntent =
//        AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setIsSmartLockEnabled(false)
//            .build()
//    signInLauncher.launch(signInIntent)
// }

// OutlinedButton(
// onClick = {
//    // Sign out logic
//    AuthUI.getInstance().delete(context)
//    signedIn =
//        false // Update the UI state to reflect that the user is no longer
//    // signed in
// },
// modifier =
// Modifier.height(50.dp).fillMaxWidth().padding(horizontal = 32.dp)) {
//    Text(
//        "Forget Google Account",
//        color = Color.Blue,
//        fontWeight = FontWeight.Medium)
// }