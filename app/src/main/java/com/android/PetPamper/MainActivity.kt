package com.android.PetPamper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.ui.screen.SignIn
import com.android.PetPamper.ui.screen.forgotPass.EmailScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailViewModel
import com.android.PetPamper.ui.screen.register.Register
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModel
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle

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
    val firebaseConnection = FirebaseConnection()

    NavHost(navController = navController, startDestination = "LoginScreen") {
      composable("LoginScreen") { SignIn(navController) }

      composable("RegisterScreen1") { Register(signUp, navController) }

      composable("RegisterScreenGoogle/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        val signUp1 = SignUpViewModelGoogle()
        SignUpScreenGoogle(signUp1, navController, email!!)
      }

      composable("EmailScreen") { EmailScreen(emailViewModel, navController) }

      composable("HomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        val nameUser = remember { mutableStateOf("") }
        firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
          val uid = documents.documents[0]?.id.toString()
          val userViewModel = UserViewModel(uid)
          userViewModel.getNameFromFirebase { name -> nameUser.value = name }
        }
        Text(text = "Welcome ${nameUser.value}")
      }
    }
  }
}
