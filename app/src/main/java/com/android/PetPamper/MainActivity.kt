package com.android.PetPamper

import GroomerProfile
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.GroomerReviews
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.resources.distance
import com.android.PetPamper.ui.screen.chat.ChatScreenPreview
import com.android.PetPamper.ui.screen.chat.ConversationsScreen
import com.android.PetPamper.ui.screen.chat.UsersScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailScreen
import com.android.PetPamper.ui.screen.forgotPass.EmailViewModel
import com.android.PetPamper.ui.screen.groomers.GroomerHome
import com.android.PetPamper.ui.screen.register.GroomerRegister
import com.android.PetPamper.ui.screen.register.GroomerSignUpViewModel
import com.android.PetPamper.ui.screen.register.Register
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModel
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle
import com.android.PetPamper.ui.screen.users.AddPetScreen
import com.android.PetPamper.ui.screen.users.AddPetScreenViewModel
import com.android.PetPamper.ui.screen.users.BarScreen
import com.android.PetPamper.ui.screen.users.BookingScreen
import com.android.PetPamper.ui.screen.users.GroomerList
import com.android.PetPamper.ui.screen.users.GroomerReview
import com.android.PetPamper.ui.screen.users.GroomerTopBar
import com.android.PetPamper.ui.screen.users.HomeScreen
import com.android.PetPamper.ui.screen.users.MapView
import com.android.PetPamper.ui.screen.users.PetListScreen
import com.android.PetPamper.ui.screen.users.PetListViewModel
import com.android.PetPamper.ui.screen.users.ReservationConfirmation
import com.android.PetPamper.ui.screen.users.ReservationsScreen
import com.android.PetPamper.ui.screen.users.SignIn
import com.android.PetPamper.ui.screen.users.UserProfileScreen
import kotlin.math.round

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

      composable("RegisterScreen1") { Register(signUp, navController) }
      composable("RegisterScreenAlreadyGroomer") { Register(signUp, navController, true) }

      composable("RegisterScreenGoogle/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        val signUp1 = SignUpViewModelGoogle()
        SignUpScreenGoogle(signUp1, navController, email!!)
      }

      composable("GroomerRegisterScreen") { GroomerRegister(groomerSignUp, navController) }
      composable("GroomerRegisterScreenAlreadyUser") {
        GroomerRegister(groomerSignUp, navController, true)
      }
      composable("EmailScreen") { EmailScreen(emailViewModel, navController) }

      composable("HomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        AppNavigation(email)
      }
      composable("GroomerHomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        if (email != null) {
          GroomerHome(email)
        }
      }
    }
  }
}

@Composable
fun AppNavigation(email: String?) {
  val navController = rememberNavController()
  val items =
      listOf(
          BarScreen.Home,
          BarScreen.Chat,
          BarScreen.Groomers,
          BarScreen.Map,
          BarScreen.Profile,
      )

  Scaffold(
      bottomBar = {
        BottomNavigation(
            backgroundColor = Color.White, modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()) {
              val currentRoute =
                  navController.currentBackStackEntryAsState().value?.destination?.route

              items.forEach { screen ->
                val iconColor =
                    if (currentRoute == screen.route) Color(0xFF2490DF) else Color.DarkGray
                BottomNavigationItem(
                    icon = {
                      Icon(
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
                    })
              }
            }
      }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BarScreen.Home.route,
            modifier = Modifier.padding(innerPadding) // Apply the padding here
            ) {
              composable(BarScreen.Home.route) {
                val nameUser = remember { mutableStateOf("") }
                val firebaseConnection = FirebaseConnection()
                firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
                  val uid = documents.documents[0]?.id.toString()
                  val userViewModel = UserViewModel(uid)
                  userViewModel.getNameFromFirebase { name -> nameUser.value = name }
                }
                HomeScreen(navController, email)
              }

              composable("ReservationsScreen") {
                ReservationsScreen(onBackPressed = { navController.navigateUp() })
              }

              composable("PetListScreen") {
                PetListScreen(
                    viewModel = PetListViewModel(email!!, PetDataHandler()),
                    onBackPressed = { navController.navigateUp() },
                    navController = navController
                )
              }

            composable("AddPetScreen") {
                AddPetScreen(
                    viewModel = AddPetScreenViewModel(email!!, PetDataHandler()),
                    onBackPressed = { navController.navigateUp() }
                )
            }

              composable("ChatScreen") { ChatScreenPreview() }

              composable("UsersScreen") {
                UsersScreen(onBackPressed = { navController.navigateUp() }, navController)
              }

              composable("BookingScreen/{Groomer}") { backStackEntry ->
                val groomerEmail = backStackEntry.arguments?.getString("Groomer")
                if (groomerEmail != null) {
                  if (email != null) {
                    BookingScreen(groomerEmail, email, navController)
                  }
                }
              }

              composable(
                  "ReservationConfirmation/{groomerEmail}/{userEmail}/{selectedDate}/{selectedHour}") {
                      backStackEntry ->
                    ReservationConfirmation(navController, backStackEntry)
                  }

              composable(BarScreen.Chat.route) {
                ConversationsScreen(onBackPressed = { navController.navigateUp() }, navController)
              }

              composable(BarScreen.Map.route) { MapView(email!!) }
              composable(BarScreen.Profile.route) { UserProfileScreen(email!!, navController) }

              composable(BarScreen.Groomers.route) {
                val address = remember { mutableStateOf(Address("", "", "", "", LocationMap())) }
                val firebaseConnection = FirebaseConnection()
                val sampleGroomers = remember { mutableStateOf(listOf<GroomerReview>()) }
                val groomersNearby = remember { mutableStateOf(listOf<Groomer>()) }
                val groomersWithReviews = remember {
                  mutableStateOf(mapOf<Groomer, GroomerReviews>())
                }

                LaunchedEffect(email) {
                  firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
                    val uid = documents.documents[0]?.id.toString()
                    val userViewModel = UserViewModel(uid)
                    userViewModel.getAddressFromFirebase { address1 ->
                      if (address.value != address1) {
                        address.value = address1
                      }
                    }
                  }
                }

                LaunchedEffect(address.value) {
                  firebaseConnection.fetchNearbyGroomers(address.value).addOnSuccessListener {
                      groomers ->
                    groomersNearby.value = groomers
                    groomers.forEach { groomer ->
                      firebaseConnection.fetchGroomerReviews(groomer.email).addOnSuccessListener {
                          reviews ->
                        groomersWithReviews.value += (groomer to reviews)
                      }
                    }
                  }
                }

                Log.d("GroomersOutLaunched", "${groomersNearby.value}")

                LaunchedEffect(groomersNearby.value, groomersWithReviews.value) {
                  sampleGroomers.value =
                      groomersNearby.value.map { groomer ->
                        val distanceWithGroomer =
                            distance(
                                address.value.location.latitude,
                                address.value.location.longitude,
                                groomer.address.location.latitude,
                                groomer.address.location.longitude)
                        GroomerReview(
                            groomer.email,
                            groomer.name,
                            groomer.petTypes.joinToString(", "),
                            groomer.price.toString() + " CHF",
                            (round(distanceWithGroomer * 10) / 10).toString() + " km",
                            groomersWithReviews.value[groomer]?.reviewCount ?: 0,
                            groomersWithReviews.value[groomer]?.rating ?: 0.0,
                            groomer.profilePic)
                      }
                }

                Column {
                  GroomerTopBar(address.value) // Call the top bar here
                  if (sampleGroomers.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      Text(text = "No groomers found") // Show a message if there are no groomers
                    }
                  } else {
                    Log.d("Groomers", "${sampleGroomers.value}")
                    GroomerList(
                        groomers = sampleGroomers.value, navController) // Then the list of groomers
                  }
                }
              }

              composable("groomer_details/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email")
                var firebaseConnection = FirebaseConnection()
                var GroomerName = remember { mutableStateOf<Groomer>(Groomer()) }
                if (email != null) {
                  firebaseConnection.fetchGroomerData(email) { groomer ->
                    GroomerName.value = groomer
                  }
                }
                GroomerProfile(GroomerName.value, navController)
              }
            }
      }
}
