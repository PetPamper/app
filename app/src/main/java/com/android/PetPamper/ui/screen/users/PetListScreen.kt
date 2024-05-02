package com.android.PetPamper.ui.screen.users

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.PetPamper.R

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PetListScreen(onBackPressed: () -> Unit) {
  val pets =
      listOf(
          Pet(
              name = "Buddy",
              description = "A friendly golden retriever",
              dateOfBirth = "2018-05-12",
              pictureRes = R.drawable.placeholder),
          Pet(
              name = "Whiskers",
              description = "A curious tabby cat",
              dateOfBirth = "2020-03-01",
              pictureRes = R.drawable.placeholder),
          // Add more pets as needed
      )

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("Pet List") },
            navigationIcon = {
              IconButton(onClick = onBackPressed) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
              }
            })
      }) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
              items(pets) { pet -> PetCard(pet) }
            }
      }
}

@Composable
fun PetCard(pet: Pet) {
  Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = 4.dp) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Image(
          painter = painterResource(id = pet.pictureRes),
          contentDescription = null,
          modifier = Modifier.size(64.dp).clip(CircleShape))
      Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
        Text(text = pet.name, style = MaterialTheme.typography.subtitle1)
        Text(text = pet.description, style = MaterialTheme.typography.body2)
        Text(text = "Date of Birth: ${pet.dateOfBirth}", style = MaterialTheme.typography.caption)
      }
    }
  }
}

data class Pet(
    val name: String,
    val description: String,
    val dateOfBirth: String,
    @DrawableRes val pictureRes: Int
)
