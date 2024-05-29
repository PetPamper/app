package com.android.PetPamper.ui.screen.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection

@Composable
fun ReservationConfirmation(navController: NavController, backStackEntry: NavBackStackEntry) {

  val groomerEmail = backStackEntry.arguments?.getString("groomerEmail") ?: ""
  val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
  val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
  val selectedHour = backStackEntry.arguments?.getString("selectedHour") ?: ""

  val firebaseConnection = FirebaseConnection()
  val GroomerName = remember { mutableStateOf("") }
  val cost = remember { mutableStateOf<Int>(0) }
  val GroomerServices = remember { mutableListOf<String>("") }

  val groomer =
      firebaseConnection.fetchGroomerData(groomerEmail) {
        GroomerName.value = it.name
        GroomerServices.clear()
        GroomerServices.addAll(it.services)
        cost.value = it.price
      }

    //review if the app UX isnt perf
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }


  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.White),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Center) {
      /* if (isLoading.value) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
          Text("Loading...", modifier = Modifier.align(Alignment.CenterHorizontally))
      } else if (errorMessage.value.isNotEmpty()) {
          Text(
              text = "Error: ${errorMessage.value}",
              color = Color.Red,
              modifier = Modifier.align(Alignment.CenterHorizontally)
          )
      } else { */
          Image(
              painter = painterResource(id = R.mipmap.confirmation),
              contentDescription = "Confirmation Icon",
              modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally)
          )

          Text(
              text = "Appointment confirmed:",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(top = 24.dp)
          )
          Text(
              text = "Date: ${selectedDate} at ${selectedHour}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 8.dp)
          )
          Text(
              text = "Groomer: ${GroomerName.value}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 8.dp)
          )
          Text(
              text = "Services: ${GroomerServices.joinToString(", ")}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 8.dp)
          )
          Text(
              text = "Total: $${cost.value}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 8.dp)
          )
          Text(
              text = "We sent you a confirmation by email at this address: ${userEmail}",
              fontSize = 16.sp,
              modifier = Modifier.padding(top = 16.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
              onClick = { navController.navigate(BarScreen.Groomers.route) },
              modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
              colors =
              ButtonDefaults.buttonColors(
                  backgroundColor = Color(0xFF2196F3),
              ),
              shape = RoundedCornerShape(8.dp)
          ) {
              Text("Make another reservation", fontSize = 18.sp, color = Color.White)
          }
      }
  }

//}
