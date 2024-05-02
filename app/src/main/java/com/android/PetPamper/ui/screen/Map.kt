package com.github.se.bootcamp.map

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(email: String) {

  val firebaseConnection = FirebaseConnection()
  val address = remember { mutableStateOf(Address("", "", "", "", LocationMap())) }
  val groomersNearby = remember { mutableStateOf(listOf<Groomer>()) }
  val navController = rememberNavController()

  LaunchedEffect(email) {
    firebaseConnection.getUserUidByEmail(email).addOnSuccessListener { documents ->
      if (!documents.documents.isNullOrEmpty()) {
        val uid = documents.documents[0]?.id.toString()
        val userViewModel = UserViewModel(uid)
        userViewModel.getAddressFromFirebase { address1 ->
          if (address.value != address1) {
            address.value = address1
          }
        }
      } else {
        Log.w("my_warn", "user documents were empty")
        address.value = Address()
      }
    }
  }

  LaunchedEffect(address.value) {
    firebaseConnection.fetchNearbyGroomers(address.value).addOnSuccessListener { groomers ->
      groomersNearby.value = groomers
    }
  }

  // Remember the camera position state
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(LatLng(46.516, 6.63282), 5f)
  }

  GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
    for (groomer in groomersNearby.value) {
      Marker(
          state =
              MarkerState(
                  position =
                      LatLng(
                          groomer.address.location.latitude, groomer.address.location.longitude)),
          title = groomer.name,
          snippet = groomer.petTypes.joinToString(", "),
      )
    }
  }
}
