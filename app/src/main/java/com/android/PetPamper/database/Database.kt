package com.android.PetPamper.database

abstract class Database {
  abstract fun documentExists(collectionPath: String, document: String): Pair<Boolean, Boolean>

  abstract fun fetchData(collectionPath: String, document: String): Pair<Boolean, Map<String, Any>?>

  abstract fun storeData(collectionPath: String, document: String, data: Any): Boolean
}
