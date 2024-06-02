package com.android.sample

import com.android.PetPamper.database.Database
import com.android.PetPamper.database.PetData
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Dog
import com.android.PetPamper.model.Pet
import com.android.PetPamper.model.PetFactory
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PetDataTest {

  // val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures",
  // "ownerId")
  class MockPetDB : Database() {

    val _data: MutableMap<String, MutableMap<String, MutableMap<String, Any>>> = mutableMapOf()

    override fun documentExists(
        collectionPath: String,
        document: String,
        onExists: () -> Unit,
        onNotExists: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (collectionPath in _data && document in _data[collectionPath]!!) {
        onExists()
      } else {
        onNotExists()
      }
    }

    override fun fetchData(
        collectionPath: String,
        document: String,
        onSuccess: (Map<String, Any>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      val collection =
          _data[collectionPath]
              ?: run {
                onFailure(Exception())
                return
              }
      val data =
          collection[document]
              ?: run {
                onFailure(Exception())
                return
              }
      onSuccess(data)
    }

    override fun storeData(
        collectionPath: String,
        document: String,
        data: Any,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      val pet = data as PetData
      val newEntry: MutableMap<String, Any> = mutableMapOf()
      newEntry += Pair("id", pet.id)
      newEntry += Pair("petType", pet.petType)
      newEntry += Pair("name", pet.name)
      newEntry += Pair("birthYear", pet.birthYear.toLong())
      newEntry += Pair("birthMonth", pet.birthMonth.toLong())
      newEntry += Pair("birthDay", pet.birthDay.toLong())
      newEntry += Pair("description", pet.description)
      newEntry += Pair("pictures", pet.pictures)
      newEntry += Pair("ownerId", pet.ownerId)

      val newDocument = mutableMapOf(Pair(document, newEntry))

      _data += Pair(collectionPath, newDocument)

      onSuccess()
    }

    override fun storeDataNoOverride(
        collectionPath: String,
        document: String,
        data: Any,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      documentExists(
          collectionPath,
          document,
          onExists = { onFailure(Exception("")) },
          onNotExists = {
            val pet = data as PetData
            val newEntry: MutableMap<String, Any> = mutableMapOf()
            newEntry += Pair("id", pet.id)
            newEntry += Pair("petType", pet.petType)
            newEntry += Pair("name", pet.name)
            newEntry += Pair("birthYear", pet.birthYear.toLong())
            newEntry += Pair("birthMonth", pet.birthMonth.toLong())
            newEntry += Pair("birthDay", pet.birthDay.toLong())
            newEntry += Pair("description", pet.description)
            newEntry += Pair("pictures", pet.pictures)
            newEntry += Pair("ownerId", pet.ownerId)

            val newDocument = mutableMapOf(Pair(document, newEntry))

            _data += Pair(collectionPath, newDocument)

            onSuccess()
          },
          onFailure = onFailure)
    }

    override fun updateData(
        collectionPath: String,
        document: String,
        dataAsMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      val collection =
          (_data[collectionPath]
              ?: run {
                onFailure(Exception())
                return
              })
      val data =
          collection[document]
              ?: run {
                onFailure(Exception())
                return
              }
      _data[collectionPath]!![document] = dataAsMap.toMutableMap()
      onSuccess()
    }

    fun clearData() {
      _data.clear()
    }

    fun print() {
      println(_data)
    }
  }

  @Test
  fun checkStoreAndRetrieve() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)
    val petFactory = PetFactory()

    // Initialize map holding test values
    // val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures",
    // "ownerId")
    val testMap: MutableMap<String, Any> = mutableMapOf()
    testMap += Pair("id", "123")
    testMap += Pair("petType", "dog")
    testMap += Pair("name", "Snoopy")
    testMap += Pair("birthDate", LocalDate.of(1950, 10, 4))
    testMap += Pair("description", "he is a good boi")
    testMap += Pair("pictures", listOf<String>())
    testMap += Pair("ownerId", "Charlie Brown")

    val storedPet =
        Dog(
            id = testMap["id"] as String,
            name = testMap["name"] as String,
            birthDate = testMap["birthDate"] as LocalDate,
            description = testMap["description"] as String,
            pictures = testMap["pictures"] as List<String>,
            ownerId = testMap["ownerId"] as String,
        )

    var onSuccessTriggered = false
    var onFailureTriggered = false

    petDataHandler.storePetToDatabase(
        storedPet, { onSuccessTriggered = true }, errorHandler = { onFailureTriggered = true })
    assertTrue(onSuccessTriggered)
    assertFalse(onFailureTriggered)

    onSuccessTriggered = false
    onFailureTriggered = false

    var retrievedPet: Pet? = petFactory.buildPet("", "other")
    petDataHandler.retrievePetFromDatabase(
        testMap["id"] as String,
        retrievedPet,
        onSuccess = { onSuccessTriggered = true },
        errorHandler = { onFailureTriggered = true })
    assertNotNull(retrievedPet)
    assertTrue(onSuccessTriggered)
    assertFalse(onFailureTriggered)

    assertEquals(testMap["id"], retrievedPet!!.id)
    assertEquals(testMap["petType"], retrievedPet.petType.petType)
    assertEquals(testMap["name"], retrievedPet.name)
    assertEquals(testMap["birthDate"], retrievedPet.birthDate)
    assertEquals(testMap["description"], retrievedPet.description)
    assertEquals(testMap["pictures"], retrievedPet.pictures)
    assertEquals(testMap["ownerId"], retrievedPet.ownerId)
  }

  @Test
  fun checkIllegalRetrieve() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)
    val petFactory = PetFactory()

    val retrievedPet: Pet = petFactory.buildPet("", "other")

    var onSuccessTriggered = false
    var onFailureTriggered = false
    petDataHandler.retrievePetFromDatabase(
        "123",
        retrievedPet,
        onSuccess = { onSuccessTriggered = true },
        errorHandler = { onFailureTriggered = true })
    assertFalse(onSuccessTriggered)
    assertTrue(onFailureTriggered)

    // Initialize map holding test values
    // val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures",
    // "ownerId")
    val testMap: MutableMap<String, Any> = mutableMapOf()
    testMap += Pair("id", "123")
    testMap += Pair("petType", "dog")
    testMap += Pair("name", "Snoopy")
    testMap += Pair("birthDate", LocalDate.of(1950, 10, 4))
    testMap += Pair("description", "he is a good boi")
    testMap += Pair("pictures", listOf<String>())
    testMap += Pair("ownerId", "Charlie Brown")

    onSuccessTriggered = false
    onFailureTriggered = false
    petDataHandler.retrievePetFromDatabase(
        "124",
        retrievedPet,
        onSuccess = { onSuccessTriggered = true },
        errorHandler = { onFailureTriggered = true })
    assertFalse(onSuccessTriggered)
    assertTrue(onFailureTriggered)

    assertFalse(onSuccessTriggered)
    assertTrue(onFailureTriggered)
  }

  @Test
  fun checkModify() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)
    val petFactory = PetFactory()

    // Initialize map holding test values
    // val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures",
    // "ownerId")
    val testMap: MutableMap<String, Any> = mutableMapOf()
    testMap += Pair("id", "123")
    testMap += Pair("petType", "dog")
    testMap += Pair("name", "Snoopy")
    testMap += Pair("birthDate", LocalDate.of(1950, 10, 4))
    testMap += Pair("description", "he is a good boi")
    testMap += Pair("pictures", listOf<String>())
    testMap += Pair("ownerId", "Charlie Brown")

    val modifiedTestMap: MutableMap<String, Any> = mutableMapOf()
    modifiedTestMap += Pair("id", "123")
    modifiedTestMap += Pair("petType", "dog")
    modifiedTestMap += Pair("name", "Scooby")
    modifiedTestMap += Pair("birthDate", LocalDate.of(1969, 9, 13))
    modifiedTestMap += Pair("description", "he is a hungry boi")
    modifiedTestMap += Pair("pictures", listOf<String>())
    modifiedTestMap += Pair("ownerId", "Shaggy")

    val storedPet =
        Dog(
            id = testMap["id"] as String,
            name = testMap["name"] as String,
            birthDate = testMap["birthDate"] as LocalDate,
            description = testMap["description"] as String,
            pictures = testMap["pictures"] as List<String>,
            ownerId = testMap["ownerId"] as String,
        )

    // Check that the store is successful
    var onSuccessTriggered = false
    var onFailureTriggered = false
    petDataHandler.storePetToDatabase(
        storedPet, { onSuccessTriggered = true }, { onFailureTriggered = true })
    assertTrue(onSuccessTriggered)
    assertFalse(onFailureTriggered)

    // Modify attributes of the pet and store modifications in database
    storedPet.name = modifiedTestMap["name"] as String
    storedPet.birthDate = modifiedTestMap["birthDate"] as LocalDate
    storedPet.description = modifiedTestMap["description"] as String
    storedPet.pictures = modifiedTestMap["pictures"] as List<String>
    storedPet.ownerId = modifiedTestMap["ownerId"] as String

    onSuccessTriggered = false
    onFailureTriggered = false
    petDataHandler.modifyPetEntry(
        storedPet, { onSuccessTriggered = true }, { onFailureTriggered = true })
    assertTrue(onSuccessTriggered)
    assertFalse(onFailureTriggered)

    // Check that the retrieved pet's attributes are modified correctly
    onSuccessTriggered = false
    onFailureTriggered = false
    val retrievedPet = petFactory.buildPet("", "other")
    petDataHandler.retrievePetFromDatabase(
        testMap["id"] as String,
        retrievedPet,
        onSuccess = { onSuccessTriggered = true },
        errorHandler = { onFailureTriggered = true })
    assertTrue(onSuccessTriggered)
    assertFalse(onFailureTriggered)

    assertEquals(modifiedTestMap["id"], retrievedPet.id)
    assertEquals(modifiedTestMap["petType"], retrievedPet.petType.petType)
    assertEquals(modifiedTestMap["name"], retrievedPet.name)
    assertEquals(modifiedTestMap["birthDate"], retrievedPet.birthDate)
    assertEquals(modifiedTestMap["description"], retrievedPet.description)
    assertEquals(modifiedTestMap["pictures"], retrievedPet.pictures)
    assertEquals(modifiedTestMap["ownerId"], retrievedPet.ownerId)
  }
}
