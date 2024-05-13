package com.android.sample

import com.android.PetPamper.database.Database
import com.android.PetPamper.database.PetData
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Dog
import com.android.PetPamper.model.Pet
import java.time.LocalDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PetDataTest {

  // val petsFields = listOf("id", "petType", "name", "birthDate", "description", "pictures",
  // "ownerId")
  class MockPetDB : Database() {

    val _data: MutableMap<String, Map<String, Map<String, Any>>> = mutableMapOf()

    override suspend fun documentExists(
        collectionPath: String,
        document: String
    ): Pair<Boolean, Boolean> {
      return Pair(true, collectionPath in _data && document in _data[collectionPath]!!)
    }

    override suspend fun fetchData(
        collectionPath: String,
        document: String
    ): Pair<Boolean, Map<String, Any>?> {
      if (!documentExists(collectionPath, document).second) {
        return Pair(false, null)
      }
      return Pair(true, _data[collectionPath]?.get(document))
    }

    override fun storeData(collectionPath: String, document: String, data: Any): Boolean {
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

      return true
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

    var storeSuccess = false
    runBlocking { launch { storeSuccess = petDataHandler.storePetToDatabase(storedPet) } }
    assertTrue(storeSuccess)

    var retrievedPet: Pet? = null
    runBlocking {
      launch { retrievedPet = petDataHandler.retrievePetFromDatabase(testMap["id"] as String) }
    }
    assertNotNull(retrievedPet)

    assertEquals(testMap["id"], retrievedPet!!.id)
    assertEquals(testMap["petType"], retrievedPet!!.petType.petType)
    assertEquals(testMap["name"], retrievedPet!!.name)
    assertEquals(testMap["birthDate"], retrievedPet!!.birthDate)
    assertEquals(testMap["description"], retrievedPet!!.description)
    assertEquals(testMap["pictures"], retrievedPet!!.pictures)
    assertEquals(testMap["ownerId"], retrievedPet!!.ownerId)
  }

  @Test
  fun checkIllegalRetrieve() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)

    var retrievedPet1: Pet? = null
    runBlocking { launch { retrievedPet1 = petDataHandler.retrievePetFromDatabase("123") } }
    assertNull(retrievedPet1)

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

    var retrievedPet2: Pet? = null

    runBlocking {
      launch {
        petDataHandler.storePetToDatabase(storedPet)
        retrievedPet2 = petDataHandler.retrievePetFromDatabase("124")
      }
    }

    assertNull(retrievedPet2)
  }

  @Test
  fun checkIllegalStore() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)

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

    val storedPet1 =
        Dog(
            id = testMap["id"] as String,
            name = testMap["name"] as String,
            birthDate = testMap["birthDate"] as LocalDate,
            description = testMap["description"] as String,
            pictures = testMap["pictures"] as List<String>,
            ownerId = testMap["ownerId"] as String,
        )

    val storedPet2 =
        Dog(
            id = testMap["id"] as String,
            name = "Scooby",
            birthDate = testMap["birthDate"] as LocalDate,
            description = testMap["description"] as String,
            pictures = testMap["pictures"] as List<String>,
            ownerId = testMap["ownerId"] as String,
        )

    // Check that the first store is successful
    var storeSuccess = false
    runBlocking { launch { storeSuccess = petDataHandler.storePetToDatabase(storedPet1) } }
    assertTrue(storeSuccess)

    // Check that storing a pet with the same ID as an existing one is unsuccessful
    runBlocking { launch { storeSuccess = petDataHandler.storePetToDatabase(storedPet2) } }
    assertFalse(storeSuccess)

    // Check that the retrieved pet has the same attributes as the first pet
    var retrievedPet: Pet? = null
    runBlocking {
      launch { retrievedPet = petDataHandler.retrievePetFromDatabase(testMap["id"] as String) }
    }
    assertNotNull(retrievedPet)

    assertEquals(testMap["id"], retrievedPet!!.id)
    assertEquals(testMap["petType"], retrievedPet!!.petType.petType)
    assertEquals(testMap["name"], retrievedPet!!.name)
    assertEquals(testMap["birthDate"], retrievedPet!!.birthDate)
    assertEquals(testMap["description"], retrievedPet!!.description)
    assertEquals(testMap["pictures"], retrievedPet!!.pictures)
    assertEquals(testMap["ownerId"], retrievedPet!!.ownerId)
  }

  @Test
  fun checkModify() {
    val db = MockPetDB()
    val petDataHandler = PetDataHandler(db)

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
    var storeSuccess = false
    runBlocking { launch { storeSuccess = petDataHandler.storePetToDatabase(storedPet) } }
    assertTrue(storeSuccess)

    // Modify attributes of the pet and store modifications in database
    storedPet.name = modifiedTestMap["name"] as String
    storedPet.birthDate = modifiedTestMap["birthDate"] as LocalDate
    storedPet.description = modifiedTestMap["description"] as String
    storedPet.pictures = modifiedTestMap["pictures"] as List<String>
    storedPet.ownerId = modifiedTestMap["ownerId"] as String

    var modifySuccess = false
    runBlocking { launch { modifySuccess = petDataHandler.modifyPetEntry(storedPet) } }
    assertTrue(modifySuccess)

    // Check that the retrieved pet's attributes are modified correctly
    var retrievedPet: Pet? = null
    runBlocking {
      launch { retrievedPet = petDataHandler.retrievePetFromDatabase(testMap["id"] as String) }
    }
    assertNotNull(retrievedPet)

    assertEquals(modifiedTestMap["id"], retrievedPet!!.id)
    assertEquals(modifiedTestMap["petType"], retrievedPet!!.petType.petType)
    assertEquals(modifiedTestMap["name"], retrievedPet!!.name)
    assertEquals(modifiedTestMap["birthDate"], retrievedPet!!.birthDate)
    assertEquals(modifiedTestMap["description"], retrievedPet!!.description)
    assertEquals(modifiedTestMap["pictures"], retrievedPet!!.pictures)
    assertEquals(modifiedTestMap["ownerId"], retrievedPet!!.ownerId)
  }
}
