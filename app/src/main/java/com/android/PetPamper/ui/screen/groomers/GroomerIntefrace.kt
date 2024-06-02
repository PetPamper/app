package com.android.PetPamper.ui.screen.groomers

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.R
import com.android.PetPamper.connectUser
import com.android.PetPamper.model.GroomerViewModel
import com.example.PetPamper.ChannelActivity
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User

@Composable
fun GroomerHomeScreen(email: String) {
  val navItems =
      listOf(
          NavItem("Home", Icons.Default.Home, { HomeScreen(email) }),
          NavItem("Chat", Icons.Default.Chat, { ChatScreen(email) }),
          NavItem("Reservations", Icons.Default.CalendarToday, { ReservationsScreen(email) }),
          NavItem("Profile", Icons.Default.Person, { ProfileScreen(email) }))

  var selectedItem by remember { mutableStateOf(0) }

  Scaffold(
      bottomBar = {
        BottomNavigation(backgroundColor = Color.White, contentColor = Color.Gray) {
          navItems.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = {
                  Icon(
                      item.icon,
                      contentDescription = item.label,
                      modifier = Modifier.size(35.dp) // Making icons bigger
                      )
                },
                label = { Text(item.label) },
                selected = selectedItem == index,
                selectedContentColor = Color(0xFF2196F3),
                unselectedContentColor = Color.Gray,
                onClick = { selectedItem = index })
          }
        }
      }) {it
        navItems[selectedItem].screen()
      }
}

data class NavItem(val label: String, val icon: ImageVector, val screen: @Composable () -> Unit)

@Composable
fun HomeScreen(email: String) {
  // Your HomeScreen Composable content
  groomerReservations(navController = rememberNavController(), email = email)
}

@Composable
fun ChatScreen(email: String) {
  val client = ChatClient.instance()
  connectUser(
      client,
      User(
          id = email,
          name = "Groomer",
          image =
              "https://d13ddrtjxhzwjl.cloudfront.net/wp-content/uploads/2021/11/dog-needs-a-groom.jpg"),
      onSuccess = { Log.d("connection", "user connected successfully") },
      onError = { println("Error connecting user: $it") })

  val navController = rememberNavController()
  // Your ChatScreen Composable content
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
            onBackPressed = { navController.popBackStack() })
      }
      InitializationState.INITIALIZING -> {
        androidx.compose.material3.Text(text = "Initializing...")
      }
      InitializationState.NOT_INITIALIZED -> {
        androidx.compose.material3.Text(text = "Not initialized...")
      }
    }
  }
}

@Composable
fun ProfileScreen(email: String) {
  // Your ProfileScreen Composable content
  GroomerProfileScreen(navController = rememberNavController(), groomerVM = GroomerViewModel(email))
}

@Composable
fun ReservationsScreen(email: String) {
  GroomerHome(email = email)
  // Your ReservationsScreen Composable content

}
