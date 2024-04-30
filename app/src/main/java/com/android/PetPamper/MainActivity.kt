package com.android.PetPamper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.ui.screen.BarScreen
import com.android.PetPamper.ui.screen.GroomerList
import com.android.PetPamper.ui.screen.GroomerReview
import com.android.PetPamper.ui.screen.GroomerTopBar
import com.android.PetPamper.ui.screen.ReservationsScreen
import com.android.PetPamper.ui.screen.SignIn
import com.android.PetPamper.ui.screen.forgotPass.EmailScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailViewModel
import com.android.PetPamper.ui.screen.HomeScreen
import com.android.PetPamper.ui.screen.PetListScreen
import com.android.PetPamper.ui.screen.register.Register
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModel
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle

import com.github.se.bootcamp.map.MapView

import com.android.PetPamper.ui.screen.register.GroomerRegister
import com.android.PetPamper.ui.screen.register.GroomerSignUpViewModel


class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { AppNavigation() }
  }

  @Composable
  fun AppNavigation() {
    val navController = rememberNavController() // Create the NavHostController
    val signUp = SignUpViewModel()
    val groomerSignUp = GroomerSignUpViewModel()
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

      /*composable("HomeScreen") {
        AppNavigation()

      }*/
      composable("GroomerRegisterScreen") {
        GroomerRegister(groomerSignUp, navController) }

      composable("EmailScreen") { EmailScreen(emailViewModel, navController) }

      composable("HomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        AppNavigation(email)
        //HomeScreen(navController, email)
        }

//      composable("ReservationsScreen") { //backStackEntry ->
//        //val email = backStackEntry.arguments?.getString("email")
//        //AppNavigation(email)
//        ReservationsScreen(
//          onBackPressed = { navController.navigateUp() }
//        )
//      }
//
//      composable("PetListScreen") {
//        //val email = backStackEntry.arguments?.getString("email")
//        //AppNavigation(email)
//        PetListScreen(
//          onBackPressed = { navController.navigateUp() }
//        )
//
//      }

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
        modifier = Modifier
          .height(60.dp)
          .fillMaxWidth()
      ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
          val iconColor = if (currentRoute == screen.route) Color(0xFF2490DF) else Color.DarkGray
          BottomNavigationItem(
            icon = { Icon(
              painterResource(id = screen.icon),
              contentDescription = null,
              modifier = Modifier
                .size(40.dp)
                .padding(bottom = 4.dp, top = 7.dp),
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
        // Text(text = "Welcome ${nameUser.value}")
        HomeScreen(navController, email)

      }

      composable("ReservationsScreen") { //backStackEntry ->
        //val email = backStackEntry.arguments?.getString("email")
        //AppNavigation(email)
        ReservationsScreen(
          onBackPressed = { navController.navigateUp() }
        )
      }

      composable("PetListScreen") {
        //val email = backStackEntry.arguments?.getString("email")
        //AppNavigation(email)
        PetListScreen(
          onBackPressed = { navController.navigateUp() }
        )

      }

      composable(BarScreen.Chat.route) { /* Search screen content */ }
      composable(BarScreen.Map.route) { MapView() }
      composable(BarScreen.Profile.route) { /* Profile screen content */ }
      composable(BarScreen.Groomers.route) {

        val address = remember { mutableStateOf(Address("", "", "", "")) }
        val firebaseConnection = FirebaseConnection()
        firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
          val uid = documents.documents[0]?.id.toString()
          val userViewModel = UserViewModel(uid)
          userViewModel.getAddressFromFirebase { address1 -> address.value = address1 }
        }

        val sampleGroomers = listOf(
        GroomerReview("Will Parker", "Dog, Cat", "50$", "1,5 KM", 26, 4.4, "https://img.freepik.com/psd-gratuit/personne-celebrant-son-orientation-sexuelle_23-2150115662.jpg"),
        GroomerReview("Kobe Bryant", "Dog, Cat, Hamster", "65$", "2 KM", 13, 4.5, "https://www.livreshebdo.fr/sites/default/files/styles/article_principal/public/assets/images/106092057_1566487914671gettyimages_1095029036.jpeg?itok=KQgvBUB3"),
        GroomerReview("Cristiano Ronaldo", "Dog", "35$", "3 KM", 2, 4.4, "https://cdn-s-www.ledauphine.com/images/0A36430E-64F8-4FC1-A61F-6BEDB90FDC94/NW_raw/le-depart-de-cristiano-ronaldo-vers-la-juventus-turin-a-ete-officialise-par-le-real-madrid-mardi-soir-quelques-heures-avant-la-demi-finale-de-coupe-du-monde-france-belgique-photo-ander-gillenea-afp-1531297805.jpg"),
        GroomerReview("Lionel Messi", "Dog, Cat", "20$", "4 KM", 56, 4.5, "https://www.ami-sportif.com/wp-content/uploads/2023/03/3502507-71397308-2560-1440.jpg"),
        GroomerReview("Pedri", "Dog, Hamster", "50$", "5 KM", 24, 4.9, "https://www.coachesvoice.com/wp-content/webpc-passthru.php?src=https://www.coachesvoice.com/wp-content/uploads/2021/10/PedriMobile-1.jpg&nocache=1"),
        GroomerReview("Lamine Yamal", "Dog", "45$", "8 KM", 12, 4.1, "https://media.cnn.com/api/v1/images/stellar/prod/230821094444-02-lamine-yamal-youngest-starter-barcelona.jpg?c=16x9&q=h_833,w_1480,c_fill"),
        GroomerReview("Cristiano Ronaldo", "Dog", "35$", "10 KM", 44, 3.9, "https://cdn-s-www.ledauphine.com/images/0A36430E-64F8-4FC1-A61F-6BEDB90FDC94/NW_raw/le-depart-de-cristiano-ronaldo-vers-la-juventus-turin-a-ete-officialise-par-le-real-madrid-mardi-soir-quelques-heures-avant-la-demi-finale-de-coupe-du-monde-france-belgique-photo-ander-gillenea-afp-1531297805.jpg")
      )

        Column {
          GroomerTopBar(address.value) // Call the top bar here
          GroomerList(groomers = sampleGroomers) // Then the list of groomers
        }

      }
      // Define other composable screens for your app
    }
  }
}


