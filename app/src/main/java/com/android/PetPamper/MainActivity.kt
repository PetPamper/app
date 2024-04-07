package com.android.PetPamper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.ui.screen.SignIn

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { AppNavigation() }
  }

  @Composable
  fun AppNavigation() {
    val navController = rememberNavController() // Create the NavHostController

    NavHost(navController = navController, startDestination = "LoginScreen") {
      composable("LoginScreen") { SignIn(navController) }
      composable("RegisterScreen") { Text("salam") }
    }
  }
}
