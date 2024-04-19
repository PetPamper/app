package com.android.PetPamper

import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.compose.material3.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.ui.screen.BarScreen
import com.android.PetPamper.ui.screen.SignIn
import com.android.PetPamper.ui.screen.forgotPass.EmailScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailViewModel
import com.android.PetPamper.ui.screen.register.Register
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModel
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle
import com.github.se.bootcamp.map.MapView
import com.google.android.material.bottomnavigation.BottomNavigationItemView

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

      composable("RegisterScreen1") { Register(1, signUp, navController) }

      composable("RegisterScreenGoogle/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        val signUp1 = SignUpViewModelGoogle()
        SignUpScreenGoogle(signUp1, navController, email!!)
      }

      composable("EmailScreen") { EmailScreen(emailViewModel, navController) }

      composable("HomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        AppNavigation(email)
        }

      }
    }


  }


@Composable
fun AppNavigation(email : String?) {
  val navController = rememberNavController()
  val items = listOf(
    BarScreen.Home,
    BarScreen.Chat,
    BarScreen.Groomers,
    BarScreen.Map,
    BarScreen.Profile,
  )

  Scaffold(
    bottomBar = {
      BottomNavigation(
        backgroundColor = Color.White,
        modifier = Modifier.height(60.dp).fillMaxWidth()
      ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
          val iconColor = if (currentRoute == screen.route) Color(0xFF2490DF) else Color.DarkGray
          BottomNavigationItem(
            icon = { Icon(
              painterResource(id = screen.icon),
              contentDescription = null,
              modifier = Modifier.size(40.dp).padding(bottom = 4.dp, top = 7.dp),
              tint = iconColor)
                   },
            label = { Text(text = screen.label, fontSize = 13.sp, color = iconColor) },
            selected = currentRoute == screen.route,
            onClick = {
              navController.navigate(screen.route) {
                navController.graph.startDestinationRoute?.let { route ->
                  popUpTo(route) { saveState = true }
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = BarScreen.Home.route,
      modifier = Modifier.padding(innerPadding)  // Apply the padding here
    ) {

      composable(BarScreen.Home.route) {

        val nameUser = remember { mutableStateOf("") }
        val firebaseConnection = FirebaseConnection()
        firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
          val uid = documents.documents[0]?.id.toString()
          val userViewModel = UserViewModel(uid)
          userViewModel.getNameFromFirebase { name -> nameUser.value = name }
        }
        Text(text = "Welcome ${nameUser.value}")

      }

      composable(BarScreen.Chat.route) { /* Search screen content */ }
      composable(BarScreen.Map.route) { MapView() }
      composable(BarScreen.Profile.route) { /* Profile screen content */ }
      composable(BarScreen.Groomers.route) { /* Settings screen content */ }
      // Define other composable screens for your app
    }
  }
}


