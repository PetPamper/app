package com.android.PetPamper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.ui.screen.RegisterScreen.SignUpScreen
import com.android.PetPamper.ui.screen.RegisterScreen.SignUpViewModel
import com.android.PetPamper.ui.screen.SignIn
import com.android.PetPamper.ui.screen.forgotPass.EmailScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { AppNavigation() }
  }

  @Composable
  fun AppNavigation() {
    val navController = rememberNavController() // Create the NavHostController
    val signUp = SignUpViewModel()
    val emailViewModel = EmailViewModel()

    NavHost(navController = navController, startDestination = "LoginScreen") {
      composable("LoginScreen") { SignIn(navController) }
      composable("RegisterScreen1") { SignUpScreen(signUp, navController) }
      composable("EmailScreen") { EmailScreen(emailViewModel,navController) }
    }
  }
}
