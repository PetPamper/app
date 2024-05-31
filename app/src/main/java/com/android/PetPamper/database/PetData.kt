package com.android.PetPamper.database

import android.util.Log
import com.android.PetPamper.model.Pet
import com.android.PetPamper.model.PetFactory
import com.android.PetPamper.model.PetType
import com.google.firebase.firestore.Filter
import java.time.LocalDate
import java.util.UUID

/** Class representing pet data entry in Firestore */
data class PetData(
    var id: String = "",
    val petType: String = PetType.OTHER.petType,
    val name: String = "Unnamed",
    val birthYear: Int = 0,
    val birthMonth: Int = 1,
    val birthDay: Int = 1,
    val description: String = "",
    val pictures: List<String> = listOf(""),
    val ownerId: String = ""
) {
  constructor(
      pet: Pet
  ) : this(
      pet.id,
      pet.petType.petType,
      pet.name,
      pet.birthDate.year,
      pet.birthDate.monthValue,
      pet.birthDate.dayOfMonth,
      pet.description,
      pet.pictures,
      pet.ownerId) {}
}

const val petsDBPath = "pets"
val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures", "ownerId")

class PetDataHandler(private val db: Database = FirebaseConnection.getInstance()) {

  /**
   * Converts map representing pet to Pet object
   *
   * @param data map containing the pet's data
   * @return pet object initialized from the map's values
   */
  private fun petFromMap(data: Map<String, Any>): Pet {
    val petFactory = PetFactory()
    val pet = petFactory.buildPet(data["id"] as String, data["petType"] as String)

    pet.name = data["name"] as String
    val birthYear = data["birthYear"] as Long
    val birthMonth = data["birthMonth"] as Long
    val birthDay = data["birthDay"] as Long
    pet.birthDate = LocalDate.of(birthYear.toInt(), birthMonth.toInt(), birthDay.toInt())
    pet.description = data["description"] as String
    pet.pictures = data["pictures"] as List<String>
    pet.ownerId = data["ownerId"] as String

    return pet
  }

  /**
   * Retrieves a pet from the database
   *
   * @param petId ID of the pet to retrieve from the database
   * @return pet object corresponding to the pet retrieved from the database if successful, or null
   *   if unsuccessful
   */
  suspend fun retrievePetFromDatabase(petId: String): Pet? {
    var (success, data) = db.fetchData(petsDBPath, petId)
    if (!success) {
      return null
    }
    data = data!!

    return petFromMap(data)
  }

  /**
   * Retrieves owner's pets as a list
   *
   * @param ownerId ID of the pet owner to retrieve pets from
   * @return list of pets from owner if successful, or null if unsuccessful
   */
  suspend fun retrievePetsFromOwner(ownerId: String): List<Pet>? {
    if (db !is FirebaseConnection) {
      throw Exception("Query is not supported")
    }

    val (success, data) = db.query(petsDBPath, Filter.equalTo("ownerId", ownerId))
    if (!success) {
      return null
    }

    return data.map { petData -> petFromMap(petData!!) }
  }

  /**
   * Stores a pet to the database
   *
   * @param pet pet to store to the database
   * @return success of the store operation
   */
  suspend fun storePetToDatabase(pet: Pet): Boolean {
    return storePetToDatabase(PetData(pet))
  }

  private suspend fun storePetToDatabase(pet: PetData): Boolean {
    val (success, petFound) = db.documentExists(petsDBPath, pet.id)
    if (petFound) {
      Log.e("IllegalStore", "Tried to store a pet that already exists in the database")
      return false
    }
    if (!success) {
      Log.e("StoreUnsuccessful", "Storing pet wasn't successful")
      return false
    }
    return db.storeData(petsDBPath, pet.id, pet)
  }

  /**
   * Modifies a pet entry in the database
   *
   * @param pet modified pet to store to the database
   * @return success of the modification
   */
  suspend fun modifyPetEntry(pet: Pet): Boolean {
    return modifyPetEntry(PetData(pet))
  }

  private suspend fun modifyPetEntry(pet: PetData): Boolean {
    val (success, petFound) = db.documentExists(petsDBPath, pet.id)
    if (!petFound) {
      Log.e("IllegalModification", "Tried to modify a pet that isn't in the database")
      return false
    }
    if (!success) {
      return false
    }
    return db.storeData(petsDBPath, pet.id, pet)
  }

  /**
   * Function that generates a UID for a newly registered pet
   *
   * @return UID as a string
   */
  fun generateNewId(): String {
    return UUID.randomUUID().toString()
  }
}
