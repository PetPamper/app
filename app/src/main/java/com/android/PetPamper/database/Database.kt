package com.android.PetPamper.database

interface Database {
    /**
     * Function that checks whether an entry exists in a collection
     *
     * @param collectionPath path to the collection
     * @param document identifier of the document that we want to verify the existence of
     * @return a pair containing: - the success of the operation
     * - whether the document was found or not
     */
  suspend fun documentExists(
      collectionPath: String,
      document: String
  ): Pair<Boolean, Boolean>

    /**
     * General function to retrieve data from the database
     *
     * @param collectionPath path to the collection (generally its name) containing the data
     * @param document identifier of the document to retrieve data from
     * @return pair containing - the success status and - the retrieved data if successful, otherwise
     *   null
     */
  suspend fun fetchData(
      collectionPath: String,
      document: String
  ): Pair<Boolean, Map<String, Any>?>

    /**
     * General function to store data to the database
     *
     * @param collectionPath path to the collection (generally its name) to store data to
     * @param document identifier of the document to be stored
     * @param data object containing the data to store
     * @return success status of the store operation
     */
  fun storeData(collectionPath: String, document: String, data: Any): Boolean
}
