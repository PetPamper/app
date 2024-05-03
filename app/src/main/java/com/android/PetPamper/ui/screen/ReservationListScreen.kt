package com.android.PetPamper.ui.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.PetPamper.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ReservationsScreen(onBackPressed: () -> Unit) {
  val groomers =
      listOf<com.android.PetPamper.ui.screen.Groomer>(
          com.android.PetPamper.ui.screen.Groomer("Hamid", listOf("Skafandri"), 12.0, 12.0, 1),
          com.android.PetPamper.ui.screen.Groomer("Sa3id", listOf("Skafandri"), 12.0, 12.0, 1),
          com.android.PetPamper.ui.screen.Groomer("3bdellah", listOf("Skafandri"), 12.0, 12.0, 1),
          com.android.PetPamper.ui.screen.Groomer("Dawya", listOf("Skafandri"), 12.0, 12.0, 1),
      )

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
              items(groomers) { groomer -> GroomerCard(groomer) }
            }
      }
}

@Composable
fun GroomerCard(groomer: Groomer) {
  Card(
      // elevation = 4.dp,
      modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.placeholder),
              contentDescription = null,
              modifier = Modifier.size(64.dp).clip(CircleShape))
          Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(text = groomer.name, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Groomable Pet Types: ${groomer.petTypes.joinToString()}",
                style = MaterialTheme.typography.labelMedium)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(text = "${groomer.price}/hour", style = MaterialTheme.typography.labelMedium)
            Text(text = "${groomer.distance} KM", style = MaterialTheme.typography.labelSmall)
            Text(text = "${groomer.reviews} Reviews", style = MaterialTheme.typography.labelMedium)
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
