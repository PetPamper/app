package com.github.se.bootcamp.map

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
fun MapView() {

    // Remember the camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.516, 6.63282), 5f)
    }

    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {

            Marker(
                state = MarkerState(position = LatLng(46.516, 6.63282)
                ))
    }
}