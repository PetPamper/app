package com.android.PetPamper.ui.screen.users

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.PetPamper.model.User


@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
    user: User
) {
    
  var _email = remember { mutableStateOf<String>(user.email) }
  var _phone = remember { mutableStateOf<String>(user.phoneNumber) }
  var _name = remember { mutableStateOf<String>(user.name) }


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
                    readOnly = true, // {TODO: fix database UUID }
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