package com.android.PetPamper.ui.screen.users

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.model.User
import com.android.PetPamper.viewmodel.LocationViewModel


@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
    user: User
) {
    
  var _email = remember { mutableStateOf<String>(user.email) }
  var _phone = remember { mutableStateOf<String>(user.phoneNumber) }
  var _name = remember { mutableStateOf<String>(user.name) }

  Log.d("EditProfile", "phone: ${_phone.value}")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = _name.value,
                    onValueChange = {
                        _name.value = it
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(
                    value = _email.value,
                    onValueChange = {},
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
                OutlinedTextField(
                    value = _phone.value,
                    onValueChange = { _phone.value = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
            }
        },
        confirmButton = {
            androidx.compose.material.Button(
                onClick = {
                    val name = _name.value
                    val email = _email.value
                    val phone = _phone.value
                    onSave(User(name,email,phone,user.address,user.pawPoints,user.profilePictureUrl))
                }) {
                Text("Save")
            }
        },
        dismissButton = { androidx.compose.material.Button(onClick = onDismiss) { Text("Cancel") } }).also { Log.d("EditProfile","Alert.also()") }


}