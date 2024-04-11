package com.android.PetPamper.database

import com.android.PetPamper.model.User

import com.google.android.gms.tasks.Task
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

  fun getUserData(uid: String): Task<DocumentSnapshot> {
    return db.collection("users").document(uid).get()
  }

  fun getUserUidByEmail(email: String): Task<QuerySnapshot> {
    return db.collection("users").whereEqualTo("email", email).get()
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
        onSuccess()
      } else {
        onFailure(task.exception ?: Exception("Login failed"))
      }
    }
  }

    fun resetUserPassword( password: String,password2: String){

    }

    fun verifyObCode(oobCode:String):Boolean{
        return FirebaseAuth.getInstance().verifyPasswordResetCode(oobCode).isSuccessful
    }

    fun restPasswordSendEmail(email: String) :Boolean {
        var res = false
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    res = true
                }
            }
        return res
    }
}
