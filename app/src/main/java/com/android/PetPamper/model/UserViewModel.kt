package com.android.PetPamper.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection

class UserViewModel(val uid: String) : ViewModel() {
  var name: String = ""
  var email: String = uid
  var phoneNumber: String = ""
  var address: Address = Address("", "", "", "", LocationMap())
  private val firebaseConnection: FirebaseConnection = FirebaseConnection()

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
          phoneNumber = document.getString("phoneNumber") ?: ""
          onComplete(phoneNumber)
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
          val address = Address(
            street,
            city,
            state,
            postalCode,
            LocationMap(
              location["latitude"] as Double,
              location["longitude"] as Double,
              location["name"] as String
            )
          )
          onComplete(address)
        }
      } else {
        // Handle the error or complete with a default Address
        onComplete(Address("", "", "", "", LocationMap()))
      }
    }
  }

  fun updateAddress(newAddress: Address, onComplete: () -> Unit) {
    address = newAddress
    val addressData = mapOf(
      "address.street" to newAddress.street,
      "address.city" to newAddress.city,
      "address.state" to newAddress.state,
      "address.postalCode" to newAddress.postalCode,
      "address.location.latitude" to newAddress.location.latitude,
      "address.location.longitude" to newAddress.location.longitude,
      "address.location.name" to newAddress.location.name
    )

    firebaseConnection.updateUserAddress(uid, addressData, onComplete)
  }
}
