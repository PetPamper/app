package com.android.PetPamper.database

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

abstract class Database {
  abstract fun documentExists(
      collectionPath: String,
      document: String,
      onExists: () -> Unit,
      onNotExists: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  abstract fun fetchData(
      collectionPath: String,
      document: String,
      onSuccess: (Map<String, Any>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  abstract fun storeData(
      collectionPath: String,
      document: String,
      data: Any,
      onSuccess: () -> Unit = { Log.d("Database", "Data successfully stored") },
      onFailure: (Exception) -> Unit = { _ -> Log.e("Database", "Could not store data") }
  )

  abstract fun updateData(
      collectionPath: String,
      document: String,
      dataAsMap: Map<String, Any>,
      onSuccess: () -> Unit = { Log.d("Database", "Data successfully updated") },
      onFailure: (Exception) -> Unit = { Log.e("Database", "Could not update data") }
  )
}
