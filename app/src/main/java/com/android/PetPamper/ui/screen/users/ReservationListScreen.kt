package com.android.PetPamper.ui.screen.users

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Reservation

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReservationsScreen(onBackPressed: () -> Unit, reservations: List<Reservation>) {

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Reservations List") },
            navigationIcon = {
              IconButton(onClick = onBackPressed) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
              }
            })
      }) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              items(reservations) { reservation -> GroomerCard(reservation) }
            }
      }
}

@Composable
fun GroomerCard(reservation: Reservation) {
  val firebaseConnection = FirebaseConnection()
  val groomerName = remember { mutableStateOf("") }
  firebaseConnection.fetchGroomerData(reservation.groomerEmail) {
    println("Groomer Name: $it")
    groomerName.value = it.name
  }

  Card(
      // elevation = 4.dp,
      modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.placeholder),
              contentDescription = null,
              modifier = Modifier.size(64.dp).clip(CircleShape))
          Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(text = groomerName.value, style = MaterialTheme.typography.titleSmall)
            Text(text = "Date: ${reservation.date}", style = MaterialTheme.typography.labelMedium)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "${reservation.hour}/hour", style = MaterialTheme.typography.labelMedium)
            Text(
                text = "${reservation.groomerEmail}/hour",
                style = MaterialTheme.typography.labelMedium)
          }
        }
      }
}

data class Groomer(
    val name: String,
    val petTypes: List<String>,
    val price: Double,
    val distance: Double,
    val reviews: Int
)
