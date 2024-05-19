package com.android.PetPamper

import GroomerProfile
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.*
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.resources.distance
import com.android.PetPamper.ui.screen.chat.*
import com.android.PetPamper.ui.screen.forgotPass.*
import com.android.PetPamper.ui.screen.groomers.GroomerHome
import com.android.PetPamper.ui.screen.register.GroomerRegister
import com.android.PetPamper.ui.screen.register.GroomerSignUpViewModel
import com.android.PetPamper.ui.screen.register.Register
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModel
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle

import com.android.PetPamper.ui.screen.users.*
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import com.example.PetPamper.ChannelActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.core.internal.exhaustive
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.Message
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.extensions.watchChannelAsState
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

import com.android.PetPamper.ui.chat.ChatListScreen
import com.android.PetPamper.ui.chat.SingleChatScreen


class MainActivity : ComponentActivity() {
    private lateinit var client: ChatClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
        val statePluginFactory = StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)

        client = ChatClient.Builder("cqxfgwz78trh", this)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in production
            .build()



        setContent {
            AppNavigation(client)
        }
    }


}

fun createChannel(client: ChatClient, userId: String, groomerId: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
    Log.d("salam", "Creating channel")
    val channelId = "dm_channel_id"
    val channelClient = client.channel(
        channelType = "messaging",
        channelId = channelId
    )

    val members = listOf(userId, groomerId)
    Log.d("Members", "Members: $members")
    val extraData = mapOf("name" to "Direct Message between $userId and $groomerId")


    channelClient.create(members, extraData).enqueue { result ->
        if (result.isSuccess) {
            val channel: Channel = result.getOrThrow()

            channel.members.forEach {
                Log.d("salam", "Member: ${it.user.id}")
            }
            Log.d("salam", "current user: ${client.getCurrentUser()}")
            Log.d("salam", "Channel created: ${channel.cid}")

            val message = Message(text = "Hello, this is a message.")


            onSuccess(channel.cid)
        } else {
            Log.e("salam", "Error creating channel: ")
            onError(result.errorOrNull()?.message ?: "Unknown error")
        }
    }
}

fun connectUser(client: ChatClient, user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {

    val token = client.devToken(user.id) // Ensure the token is correct

    if (client.getCurrentUser() != null) {
        Log.d("ChatApp", "User already connected")
        onSuccess()
    } else {
        client.connectUser(user, token).enqueue { result ->
            if (result.isSuccess) {
                Log.d("ChatApp", "User connected: ${user.id}")
                onSuccess()
            } else {
                Log.e("ChatApp", "Error connecting user: ${result.errorOrNull()?.message}")
                onError(result.errorOrNull()?.message ?: "Unknown error")
            }
        }
    }
}




    @Composable
  fun AppNavigation(client: ChatClient) {
    val navController = rememberNavController()
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
        AppNavigation(email, client)
      }
      composable("GroomerHomeScreen/{email}") { backStackEntry ->
        val email = backStackEntry.arguments?.getString("email")
        if (email != null) {

          GroomerHome(email)
        }
      }
    }
  }




@Composable
fun AppNavigation(email: String?, client: ChatClient) {
  val navController = rememberNavController()
  val items =
      listOf(
          BarScreen.Home,
          BarScreen.Chat,
          BarScreen.Groomers,
          BarScreen.Map,
          BarScreen.Profile,
      )



    val user1Id = remember { mutableStateOf("alilebg@gmail.com") }

    val firebaseConnection = FirebaseConnection()

    LaunchedEffect(email) {
        if (email != null) {
            firebaseConnection.fetchChatId(email, onComplete = { userName, userId ->
                user1Id.value = userId
                val user1 = User(
                    id = userId,
                    name = userName,
                    image = "https://e7.pngegg.com/pngimages/81/556/png-clipart-graphy-graphy-royalty-free-microphone-child.png",
                )

                connectUser(client, user1, onSuccess = {
                     Log.d("ChatApp", "User connected")
                }, onError = {
                     Log.e("ChatApp", "Error connecting user: $it")
                })

            })
        }
    }



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
            modifier = Modifier.padding(innerPadding)) {
              composable(BarScreen.Home.route) {
                val nameUser = remember { mutableStateOf("") }
                val firebaseConnection = FirebaseConnection()
                firebaseConnection.getUserUidByEmail(email!!).addOnSuccessListener { documents ->
                  val uid = documents.documents[0]?.id.toString()
                  val userViewModel = UserViewModel(uid)
                  userViewModel.getNameFromFirebase { name ->
                      nameUser.value = name }
                }
                HomeScreen(navController, email)
              }

              composable("ReservationsScreen") {
                  val reservations = remember { mutableStateOf(listOf<Reservation>()) }
                  firebaseConnection.fetchReservations(email!!) { res ->
                      reservations.value = res
                  }

                ReservationsScreen(reservations = reservations.value, onBackPressed = { navController.navigateUp() })
              }

              composable("PetListScreen") {
                PetListScreen(
                    viewModel = PetListViewModel(email!!, PetDataHandler()),
                    onBackPressed = { navController.navigateUp() },
                    navController = navController)
              }

              composable("AddPetScreen") {
                AddPetScreen(
                    viewModel = AddPetScreenViewModel(email!!, PetDataHandler()),
                    onBackPressed = { navController.navigateUp() })
              }

              //composable("ChatScreen") { ChatScreenPreview() }
            // New added chat

//            composable("SingleChatScreen/{chatId}") {
//                val chatId = it.arguments?.getString("chatId")
//                chatId?.let {
//                    SingleChatScreen(navController = navController, vm = vm, chatId = it)
//                }
//            }

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
                val clientInitialisationState by client.clientState.initializationState.collectAsState()

                ChatTheme {
                    when (clientInitialisationState) {
                        InitializationState.COMPLETE -> {
                            val context = LocalContext.current
                            ChannelsScreen(
                                title = stringResource(id = R.string.app_name),
                                isShowingSearch = true,
                                onItemClick = { channel ->
                                    context.startActivity(ChannelActivity.getIntent(context, channel.cid))
                                },
                                onBackPressed = { navController.popBackStack() }
                            )
                        }
                        InitializationState.INITIALIZING -> {
                            Text(text = "Initializing...")
                        }
                        InitializationState.NOT_INITIALIZED -> {
                            Text(text = "Not initialized...")
                        }

                    }
                }
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

                LaunchedEffect(groomersNearby.value, groomersWithReviews.value, address.value) {
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
                  GroomerTopBar(
                      address.value,
                      onUpdateAddress = { updatedAddress ->
                        address.value = updatedAddress
                        if (email != null) {
                          firebaseConnection.changeAddress(email, updatedAddress)
                        }
                      })
                  if (sampleGroomers.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      Text(text = "No groomers found")
                    }
                  } else {
                    Log.d("Groomers", "${sampleGroomers.value}")
                    GroomerList(groomers = sampleGroomers.value, navController)
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
                GroomerProfile(GroomerName.value, navController, user1Id.value, client)
              }
            }
      }
}


