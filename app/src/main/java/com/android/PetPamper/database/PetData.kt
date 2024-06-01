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

class PetDataHandler(private val db: Database) {

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

    private fun petToMap(pet: Pet): Map<String, Any> {
        return mapOf(
            "name" to pet.name,
            "birthYear" to pet.birthDate.year,
            "birthMonth" to pet.birthDate.monthValue,
            "birthDay" to pet.birthDate.dayOfMonth,
            "description" to pet.description,
            "pictures" to pet.pictures,
            "ownerId" to pet.ownerId
        )
    }
    
    private fun copyFromPet(oldPet: Pet?, newPet: Pet?) {
        if (newPet != null) {
            oldPet?.id = newPet.id
            oldPet?.name = newPet.name
            oldPet?.petType = newPet.petType
            oldPet?.birthDate = newPet.birthDate
            oldPet?.description = newPet.description
            oldPet?.pictures = newPet.pictures
            oldPet?.ownerId = newPet.ownerId
        }
    }

  /**
   * Retrieves a pet from the database
   *
   * @param petId ID of the pet to retrieve from the database
   * @return pet object corresponding to the pet retrieved from the database if successful, or null
   *   if unsuccessful
   */
  fun retrievePetFromDatabase(petId: String, receivedPet: Pet?, errorHandler: (Exception) -> Unit = {}) {
    db.fetchData(
        petsDBPath, 
        petId, 
        onSuccess = { data -> copyFromPet(receivedPet, petFromMap(data)) },
        onFailure = { exception ->
            Log.e("PetData", "Could not retrieve pet from database. Error: $exception")
            errorHandler(exception)
        }
    )
  }

  /**
   * Retrieves owner's pets as a list
   *
   * @param ownerId ID of the pet owner to retrieve pets from
   * @return list of pets from owner if successful, or null if unsuccessful
   */
  fun retrievePetsFromOwner(ownerId: String, receivedPetList: MutableList<Pet>, errorHandler: (Exception) -> Unit = {}) {
    if (db !is FirebaseConnection) {
      throw Exception("Query is not supported")
    }

      receivedPetList.clear()

    db.query(
        petsDBPath, 
        Filter.equalTo("ownerId", ownerId), 
        onSuccess = { dataList -> dataList.forEach { petData -> receivedPetList.add(petFromMap(petData)) }},
        onFailure = { exception ->
            Log.e("PetData", "Could not retrieve pets from database. Error: $exception")
            errorHandler(exception)
        })
  }

  /**
   * Stores a pet to the database
   *
   * @param pet pet to store to the database
   * @return success of the store operation
   */
  fun storePetToDatabase(pet: Pet, errorHandler: (Exception) -> Unit = {}) {
    storePetToDatabase(PetData(pet), errorHandler)
  }

  private fun storePetToDatabase(pet: PetData, errorHandler: (Exception) -> Unit = {}) {
      db.storeData(
          petsDBPath,
          pet.id,
          pet,
          onSuccess = { Log.d("PetData", "Pet was successfully stored.") },
          onFailure = { exception ->
              Log.e("PetData", "Could not store pet to database. Error: $exception")
              errorHandler(exception)
          })
  }

  /**
   * Modifies a pet entry in the database
   *
   * @param pet modified pet to store to the database
   * @return success of the modification
   */

  private fun modifyPetEntry(pet: Pet, errorHandler: (Exception) -> Unit = {}) {
      db.updateData(
          petsDBPath,
          pet.id,
          petToMap(pet),
          onSuccess = { Log.d("PetData", "Pet was successfully updated.")},
          onFailure = { exception ->
              Log.e("PetData", "Could not update pet in database. Error: $exception")
              errorHandler(exception)
          })
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
