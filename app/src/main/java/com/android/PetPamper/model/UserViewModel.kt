package com.android.PetPamper.model

import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection

class UserViewModel(uid: String) : ViewModel() {
  var uid: String = uid
  var name: String = ""
  var email: String = uid
  var phoneNumber: String = ""
  var address: Address = Address("", "", "", "", LocationMap())
  var firebaseConnection: FirebaseConnection = FirebaseConnection()

  init {
    getNameFromFirebase { name -> this.name = name }
    getPhoneNumberFromFirebase { phoneNumber -> this.phoneNumber = phoneNumber }
    getAddressFromFirebase { address -> this.address = address }
  }

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

  fun getPhoneNumberFromFirebase(onComplete: (String) -> Unit) {
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          name = document.getString("phoneNumber") ?: ""
          onComplete(name)
        }
      }
    }
  }

  fun getAddressFromFirebase(onComplete: (Address) -> Unit) {
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          // Fetch each part of the address from the document
          val street = document.getString("address.street") ?: ""
          val city = document.getString("address.city") ?: ""
          val state = document.getString("address.state") ?: ""
          val postalCode = document.getString("address.postalCode") ?: ""
          val location = document.get("address.location") as HashMap<*, *>

          // Construct an Address object
          val address =
              Address(
                  street,
                  city,
                  state,
                  postalCode,
                  LocationMap(
                      location["latitude"] as Double,
                      location["longitude"] as Double,
                      location["name"] as String))
          onComplete(address)
        }
      } else {
        // Handle the error or complete with a default Address
        onComplete(Address("", "", "", "", LocationMap()))
      }
    }
  }
}
