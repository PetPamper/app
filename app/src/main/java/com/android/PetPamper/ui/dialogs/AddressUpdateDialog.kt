package com.android.PetPamper.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.viewmodel.LocationViewModel

@Composable
fun AddressUpdateDialog(
    initialAddress: Address,
    onDismiss: () -> Unit,
    onSave: (Address) -> Unit,
    viewModel: LocationViewModel = viewModel()
) {
  var street by remember { mutableStateOf(initialAddress.street) }
  var city by remember { mutableStateOf(initialAddress.city) }
  var state by remember { mutableStateOf(initialAddress.state) }
  var postalCode by remember { mutableStateOf(initialAddress.postalCode) }
  var locationName by remember { mutableStateOf(initialAddress.location.name) }

  val coroutineScope = rememberCoroutineScope()
  var suggestions = remember { mutableStateListOf<LocationMap>() }
  var locationView = LocationViewModel()
  var location by remember { mutableStateOf(Address()) }

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Update Address") },
      text = {
        Column {
          OutlinedTextField(
              value = street,
              onValueChange = {
                street = it
                locationView.fetchLocation(street) { result ->
                  if (result != null) {
                    suggestions.clear()
                    suggestions.addAll(result)
                  }
                }
              },
              label = { Text("Street") },
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
          OutlinedTextField(
              value = city,
              onValueChange = { city = it },
              label = { Text("City") },
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
          OutlinedTextField(
              value = state,
              onValueChange = { state = it },
              label = { Text("State") },
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
          OutlinedTextField(
              value = postalCode,
              onValueChange = { postalCode = it },
              label = { Text("Postal Code") },
              modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
          if (suggestions.isNotEmpty()) {
            Text("Suggestions:", style = MaterialTheme.typography.subtitle1)
            suggestions.forEach { suggestion ->
              TextButton(
                  onClick = {
                    street = suggestion.name
                    locationName = suggestion.name
                    location = Address(suggestion.name, city, state, postalCode, suggestion)
                  }) {
                    Text(suggestion.name)
                  }
            }
          }
        }
      },
      confirmButton = {
        Button(
            onClick = {
              val updatedAddress = location
              onSave(updatedAddress)
            }) {
              Text("Save")
            }
      },
      dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } })
}
