package com.android.PetPamper.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.User

open class UserViewModel(var email: String) : ViewModel() {
    var uid = "ERROR_UUID"
    private var user = User(email = email)
    private var isLoaded = false
    private val firebaseConnection: FirebaseConnection = FirebaseConnection.getInstance()

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
                val docRef = firebaseConnection.getUserData(uid)

                docRef.addOnCompleteListener { task ->
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
            } else {
                Log.w("firebase query","Unable to find UUID from email ${user.email} ! falling back to default user")
                uid = "ERROR_UUID"
            }
        }
        if(uid != "ERROR_UUID"){
            val docRef = firebaseConnection.getUserData(uid)
            docRef.addOnCompleteListener { task ->
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
        this.user = User(name,email,phone,address,pawPoints,picURL)
        firebaseConnection.storeData("users",uid,this.user)
    }

  fun getNameFromFirebase(onComplete: (String) -> Unit) {
    firebaseConnection.getUserData(uid).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val document = task.result
        if (document != null) {
          var name = document.getString("name") ?: ""
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
          var phoneNumber = document.getString("phoneNumber") ?: ""
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
            val location = document.get("address.location") as? HashMap<*, *> ?: emptyMap<Any, Any>()


            // Construct an Address object
          val address =
              Address(
                  street,
                  city,
                  state,
                  postalCode,
                  LocationMap(
                      location["latitude"] as? Double ?: 0.0,
                      location["longitude"] as? Double ?: 0.0,
                      location["name"] as? String ?: ""))
          onComplete(address)
        }
      } else {
        // Handle the error or complete with a default Address
        onComplete(Address("", "", "", "", LocationMap()))
      }
    }
  }

  fun updateAddress(newAddress: Address, onComplete: () -> Unit) {
    var address = newAddress
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
  }
}
