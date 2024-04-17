package com.android.PetPamper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapScreen() {
    val ecublens = LatLng(46.5260, 6.5610) // Coordinates for Ecublens
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ecublens, 12f)
}

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = ecublens),
            title = "Marker in Ecublens",
            snippet = "Ecublens, Vaud, Switzerland"
        )
    }
}
