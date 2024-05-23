package com.android.PetPamper.ui.screen.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.resources.distance
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapView(userVM: UserViewModel) {
  var user by remember { mutableStateOf(userVM.getUser()) }
  val context = LocalContext.current
  val firebaseConnection = FirebaseConnection()
  var groomersNearby by remember { mutableStateOf(listOf<Groomer>()) }
  var showDialog by remember { mutableStateOf(false) }
  var selectedGroomer by remember { mutableStateOf<Groomer?>(null) }

  LaunchedEffect(userVM.uid) { user = userVM.getUser() }

  LaunchedEffect(user.address) {
    firebaseConnection.fetchNearbyGroomers(user.address).addOnSuccessListener { groomers ->
      groomersNearby = groomers
    }
  }

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.516, 6.63282), 10f)
  }

  GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
    groomersNearby.forEach { groomer ->
      Marker(
          state =
              MarkerState(
                  position =
                      LatLng(
                          groomer.address.location.latitude, groomer.address.location.longitude)),
          title = groomer.name,
          snippet = "Click for details",
          onClick = {
            selectedGroomer = groomer
            showDialog = true
            true // Return true to indicate that we have handled the click
          })
    }
  }

  if (showDialog) {
    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(text = "Groomer Details") },
        text = { selectedGroomer?.let { InfoWindow(it, user.address.location) } },
        confirmButton = {
          Button(
              colors =
                  ButtonColors(Color.LightGray, Color.LightGray, Color.LightGray, Color.LightGray),
              onClick = { showDialog = false }) {
                Text("Close")
              }
        })
  }
}

@Composable
fun InfoWindow(groomer: Groomer, userLocation: LocationMap) {
  Column(
      modifier =
          Modifier.padding(4.dp)
              .border(
                  1.dp,
                  Color.White,
                  RoundedCornerShape(50.dp)) // Increased rounding and reduced border thickness
              .padding(6.dp) // Reduced padding
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter =
                  rememberImagePainter(
                      request =
                          ImageRequest.Builder(LocalContext.current)
                              .data(groomer.profilePic)
                              .build()),
              contentDescription = "Groomer Profile Picture",
              modifier = Modifier.size(100.dp).padding(2.dp), // Reduced padding
              contentScale = ContentScale.Crop)
          Column(modifier = Modifier.padding(2.dp)) { // Reduced padding
            Text(
                text = groomer.name,
                color = Color(0xFF000080), // Navy blue color
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold)
            Text(
                text =
                    "Distance: ${
                        String.format("%.2f", distance(
                            userLocation.latitude, userLocation.longitude,
                            groomer.address.location.latitude, groomer.address.location.longitude
                        ))} km",
                fontWeight = FontWeight.Bold)
            Text(
                text = "Groomable Pets: ${groomer.petTypes.joinToString(", ")}",
                fontWeight = FontWeight.Bold)
          }
        }
      }
}
