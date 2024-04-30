package com.android.PetPamper.ui.screen

import com.android.PetPamper.R

sealed class BarScreen(val route: String, val label: String, val icon: Int) {
  object Home : BarScreen("home_screen", "Home", R.drawable.bar_home)

  object Chat : BarScreen("chat_screen", "Chat", R.drawable.bar_chat)

  object Groomers : BarScreen("groomers_screen", "Groomers", R.drawable.bar_groomers)

  object Map : BarScreen("map_screen", "Map", R.drawable.bar_map)

  object Profile : BarScreen("profile_screen", "Profile", R.drawable.bar_profile)
}
