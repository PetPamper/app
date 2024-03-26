package com.android.PetPamper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.PetPamper.resources.C
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { SignIn() }
  }

  private fun OnSignInResult(
      result: FirebaseAuthUIAuthenticationResult,
      updateUI: (Boolean, String) -> Unit
  ) {
    if (result.resultCode == RESULT_OK) {
      // Successfully signed in
      val user = FirebaseAuth.getInstance().currentUser
      //            val intent = Intent(this, HomePage::class.java).apply {
      //                // Passer l'ID utilisateur à HomePageActivity
      //                putExtra("USER_ID", user?.uid)
      //            }
      //            startActivity(intent)
      //            finish()
      updateUI(true, user?.displayName ?: "Unknown")

      // ...
    } else {
      updateUI(false, "")
    }
  }

  @Composable
  fun SignIn() {
    var signedIn by remember { mutableStateOf(false) }
    var displayName by remember { mutableStateOf("") }

    val context = LocalContext.current

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
      Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize().testTag("LoginScreen")) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().testTag("LoginScreen")) {
                  Image(
                      painter = painterResource(id = R.mipmap.petpamper_logo_foreground),
                      contentDescription = "App Logo",
                      modifier = Modifier.size(350.dp))

                  Spacer(modifier = Modifier.height(32.dp))

                  Text(
                      text = "Welcome",
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.testTag("LoginTitle"))

                  Spacer(modifier = Modifier.height(32.dp))

                  Button(
                      onClick = {
                        val signInIntent =
                            AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build()
                        signInLauncher.launch(signInIntent)
                      },
                      colors = ButtonDefaults.buttonColors(Color.White),
                      modifier =
                          Modifier.height(50.dp)
                              .fillMaxWidth()
                              .padding(horizontal = 32.dp)
                              .testTag("LoginButton"),
                  ) {
                    Icon(
                        painter =
                            painterResource(
                                id = R.mipmap.google_logo), // Make sure to have a Google logo
                        // drawable
                        contentDescription = "Sign in with Google",
                        tint = Color.Unspecified,
                        modifier = Modifier.testTag("LoginScreen"))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign in with Google", color = Color.Black, fontWeight = FontWeight.Medium)
                  }

                  OutlinedButton(
                      onClick = {
                        // Sign out logic
                        AuthUI.getInstance().delete(context)
                        signedIn =
                            false // Update the UI state to reflect that the user is no longer
                        // signed in
                      },
                      modifier =
                          Modifier.height(50.dp).fillMaxWidth().padding(horizontal = 32.dp)) {
                        Text(
                            "Forget Google Account",
                            color = Color.Blue,
                            fontWeight = FontWeight.Medium)
                      }

                  // Add the rest of your content below
                }
          }
    } else {
      Greeting(name = "PetPamper")
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier.semantics { testTag = C.Tag.greeting })
}
