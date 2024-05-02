package com.android.PetPamper.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.FirebaseConnection
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class GroomerViewModel(uid: String, email: String) : ViewModel() {
  var uid: String = uid
  var name: String = ""
  var phoneNumber: String = ""
  var address: Address = Address("", "", "", "", LocationMap())
  var availableHours: List<Calendar> = listOf()
  var firebaseConnection: FirebaseConnection = FirebaseConnection()
  var email: String = email
  var allHours: MutableState<Map<String, List<Calendar>>> = mutableStateOf(mapOf())

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

  //    fun fetchAvailableHours(onComplete: (List<Calendar>) -> Unit) {
  //        // Assume `email` is already a property of the ViewModel, or fetch it as needed
  //        firebaseConnection.getAvailableHours(email) { calendars ->
  //            // The callback now directly receives a List<Calendar> from the Firebase method
  //            availableHours = calendars
  //            onComplete(calendars)
  //        }
  //    }

  // New method to update available hours in Firebase
  fun updateAvailableHours(
      email: String,
      date: String,
      newHours: List<Calendar>,
      onComplete: () -> Unit
  ) {
    val db = FirebaseFirestore.getInstance()
    val docRef =
        db.collection("groomerAvailabilities").document(email).collection("dates").document(date)

    db.runTransaction { transaction ->
          val snapshot = transaction.get(docRef)
          val existingHours = snapshot.get("availableHours") as? List<Timestamp> ?: listOf()

          // Convert new hours to timestamps and filter out any that are already saved
          val newHoursTimestamps = newHours.map { Timestamp(it.timeInMillis / 1000, 0) }
          val filteredNewHours =
              newHoursTimestamps.filter { newHour -> !existingHours.contains(newHour) }

          // Merge existing hours with filtered new hours
          transaction.set(docRef, mapOf("availableHours" to (existingHours + filteredNewHours)))
        }
        .addOnSuccessListener { onComplete() }
        .addOnFailureListener { e ->
          println("Error updating available hours: ${e.localizedMessage}")
          onComplete()
        }
  }

  fun saveHoursToFirebase(selectedHoursMap: Map<String, List<Int>>, onComplete: () -> Unit) {
    for ((date, hours) in selectedHoursMap) {
      val hoursList =
          hours.map { hour ->
            Calendar.getInstance().apply {
              set(Calendar.YEAR, date.split("-")[0].toInt())
              set(Calendar.MONTH, date.split("-")[1].toInt() - 1) // Month is zero-based
              set(Calendar.DAY_OF_MONTH, date.split("-")[2].toInt())
              set(Calendar.HOUR_OF_DAY, hour)
              set(Calendar.MINUTE, 0)
              set(Calendar.SECOND, 0)
            }
          }
      // Check against allHours to only save new hours
      val alreadySavedHours = allHours.value[date]?.map { it.get(Calendar.HOUR_OF_DAY) } ?: listOf()
      val newHoursToSave =
          hoursList.filter { !alreadySavedHours.contains(it.get(Calendar.HOUR_OF_DAY)) }

      if (newHoursToSave.isNotEmpty()) {
        updateAvailableHours(email, date, newHoursToSave) {
          // Update local state to reflect newly saved hours
          allHours.value =
              allHours.value.toMutableMap().also {
                it[date] = (it[date] ?: listOf()) + newHoursToSave
              }
          onComplete()
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
}
