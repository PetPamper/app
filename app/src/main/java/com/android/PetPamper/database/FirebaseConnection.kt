package com.android.PetPamper.database

import android.util.Log
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.GroomerReviews
import com.android.PetPamper.model.User
import com.android.PetPamper.resources.distance
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class FirebaseConnection {

  private val db: FirebaseFirestore = Firebase.firestore

  fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("users")
        .document(user.email)
        .set(user)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun addGroomer(groomer: Groomer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("groomers")
        .document(groomer.email) // Using email as a unique identifier; adjust if needed
        .set(groomer)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun addGroomerReview(
      groomerReview: GroomerReviews,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection("groomerReviews")
        .document(groomerReview.email)
        .set(groomerReview)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun getUserData(uid: String): Task<DocumentSnapshot> {
    return db.collection("users").document(uid).get()
  }

  fun getUserUidByEmail(email: String): Task<QuerySnapshot> {
    return db.collection("users").whereEqualTo("email", email).get()
  }

  fun fetchNearbyGroomers(address: Address): Task<List<Groomer>> {
    // Create a task completion source
    val source = TaskCompletionSource<List<Groomer>>()

    // Fetch all groomers
    db.collection("groomers").get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val groomers = task.result?.toObjects(Groomer::class.java)

        val nearbyGroomers = mutableListOf<Groomer>()

        // Calculate the distance for each groomer
        groomers?.forEach { groomer ->
          val distance =
              distance(
                  address.location.latitude,
                  address.location.longitude,
                  groomer.address.location.latitude,
                  groomer.address.location.longitude)

          // If the distance is less than or equal to 10 kilometers, add the groomer to the list
          Log.d(
              "my_GroomersFirebase",
              "Distance to ${groomer.name}: $distance from ${address.location.name}")
          if (distance <= 10 && !nearbyGroomers.contains(groomer)) {
            nearbyGroomers.add(groomer)
          }
        }

        // Set the result of the task
        source.setResult(nearbyGroomers)
        Log.d("my_GroomersFirebase", "Nearby groomers: $nearbyGroomers")
      } else {
        // If the task failed, set the exception
        source.setException(task.exception ?: Exception("Failed to fetch groomers"))
      }
    }

    // Return the task
    return source.task
  }

  fun fetchGroomerReviews(email: String): Task<GroomerReviews> {
    val source = TaskCompletionSource<GroomerReviews>()

    db.collection("groomerReviews").whereEqualTo("email", email).get().addOnCompleteListener { task
      ->
      if (task.isSuccessful) {
        val reviews = task.result?.toObjects(GroomerReviews::class.java)
        if (reviews != null && reviews.isNotEmpty()) {
          source.setResult(reviews[0])
        } else {
          source.setException(Exception("No review found for this email"))
        }
      } else {
        source.setException(task.exception ?: Exception("Failed to fetch reviews"))
      }
    }

    return source.task
  }

  fun registerUser(
      email: String,
      password: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    FirebaseAuth.getInstance()
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            onSuccess()
          } else {
            onFailure(task.exception ?: Exception("Registration failed"))
          }
        }
  }

  fun loginUser(
      email: String,
      password: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
        task ->
      if (task.isSuccessful) {
        Log.d("my_debug", "user: ${task.result.user?.email ?: "NULL_USER"}")
        onSuccess()
      } else {
        onFailure(task.exception ?: Exception("Login failed"))
      }
    }
  }

  fun resetUserPassword(password: String, password2: String) {}

  fun verifyObCode(oobCode: String): Boolean {
    return FirebaseAuth.getInstance().verifyPasswordResetCode(oobCode).isSuccessful
  }

  fun restPasswordSendEmail(email: String): Boolean {
    var res = false
    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        res = true
      }
    }
    return res
  }
}
