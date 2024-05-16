package com.android.PetPamper.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection
import kotlinx.coroutines.tasks.await

open class UserViewModel(var email: String) : ViewModel() {
  var uid = "ERROR_UUID"
  private var user = User(email = email)
  private var isLoaded = false
  private val firebaseConnection: FirebaseConnection = FirebaseConnection()

    open fun getUser(force: Boolean = false): User {
        if(!isLoaded || force) {
            fetchUser()
        }
        return user
    }

  private fun fetchUser() {
      firebaseConnection.getUserUidByEmail(user.email).addOnSuccessListener{
          if(it.documents.isNotEmpty()){
            this.uid = it.documents[0].id
          } else {
              Log.w("firebase query","Unable to find UUID from email ${user.email} ! falling back to default user")
              uid = "ERROR_UUID"
          }
      }
      if(uid != "ERROR_UUID"){
          firebaseConnection.getUserData(uid).get().addOnCompleteListener { task ->
              if (task.isSuccessful) {
                  val document = task.result
                  if (document != null) {
                      this.user.name = document.getString("name") ?: ""
                      this.user.email = document.getString("email") ?: ""
                      val street = document.getString("address.street") ?: ""
                      val city = document.getString("address.city") ?: ""
                      val state = document.getString("address.state") ?: ""
                      val postalCode = document.getString("address.postalCode") ?: ""
                      val loc = document.get("address.location")
                      val location = if (loc != null) {
                          val l = loc as HashMap<*,*>
                          LocationMap(l["latitude"] as Double, l["longitude"] as Double, l["name"] as String)
                      } else {
                          LocationMap()
                      }
                      this.user.address = Address(street, city, state, postalCode, location)
                      this.user.phoneNumber = document.getString("phoneNumber") ?: ""
                      this.user.pawPoints = document.getLong("pawPoints")?.toInt() ?: 0
                      this.user.profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                  }
              }
          }
          isLoaded = true
      }
  }

  fun updateUser(name:String = user.name, email:String = user.email, phone:String = user.phoneNumber, address:Address = user.address, pawPoints:Int = user.pawPoints, picURL:String = user.profilePictureUrl) {
      firebaseConnection.storeData("users",uid,User(name,email,phone,address,pawPoints,picURL))
  }

  fun getNameFromFirebase(onComplete: (String) -> Unit) {
    firebaseConnection.getUserData(uid).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          val name = document.getString("name") ?: ""
          onComplete(name)
            user.name = name
        }
      }
    }
  }

  fun getPhoneNumberFromFirebase(onComplete: (String) -> Unit) {
    firebaseConnection.getUserData(uid).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
            val phoneNumber = document.getString("phoneNumber") ?: ""
          onComplete(phoneNumber)
            user.phoneNumber = phoneNumber
        }
      }
    }
  }

  fun getAddressFromFirebase(onComplete: (Address) -> Unit) {
    firebaseConnection.getUserData(uid).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          // Fetch each part of the address from the document
          val street = document.getString("address.street") ?: ""
          val city = document.getString("address.city") ?: ""
          val state = document.getString("address.state") ?: ""
          val postalCode = document.getString("address.postalCode") ?: ""
          val loc = document.get("address.location")
            val location = if (loc != null) {
                val l = loc as HashMap<*,*>
                LocationMap(l["latitude"] as Double, l["longitude"] as Double, l["name"] as String)
            } else {
                LocationMap()
            }

          // Construct an Address object
          val address =
              Address(
                  street,
                  city,
                  state,
                  postalCode,
                  location)
          onComplete(address)
        }
      } else {
        // Handle the error or complete with a default Address
        onComplete(Address("", "", "", "", LocationMap()))
      }
    }
  }

  fun updateAddress(newAddress: Address, onComplete: () -> Unit) {
      val address = newAddress
    val addressData =
        mapOf(
            "address.street" to newAddress.street,
            "address.city" to newAddress.city,
            "address.state" to newAddress.state,
            "address.postalCode" to newAddress.postalCode,
            "address.location.latitude" to newAddress.location.latitude,
            "address.location.longitude" to newAddress.location.longitude,
            "address.location.name" to newAddress.location.name)

    firebaseConnection.updateUserAddress(uid, addressData, onComplete)
      user.address = address
  }
}
