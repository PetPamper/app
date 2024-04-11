package com.android.PetPamper.model

import com.android.PetPamper.database.FirebaseConnection

class UserViewModel(uid: String) {
  var uid: String = uid
  var name: String = ""
  var email: String = ""
  var phoneNumber: String = ""
  var address: Address = Address("", "", "", "")
  var firebaseConnection: FirebaseConnection = FirebaseConnection()

  fun getNameFromFirebase(onComplete: (String) -> Unit) {
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          name = document.getString("name") ?: ""
          onComplete(name)
        }
      }
    }
  }
}
