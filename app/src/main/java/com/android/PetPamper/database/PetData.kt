package com.android.PetPamper.database

import com.android.PetPamper.model.Pet
import com.android.PetPamper.model.PetFactory
import java.time.LocalDate


val firebaseConnection = FirebaseConnection()
const val petsDBPath = "pets"
val petsFields = listOf("name", "birthDate", "description", "pictures", "owner")

suspend fun retrievePetFromDatabase(petId: String): Pet {
    val data = firebaseConnection.fetchData(petsDBPath, petId)

    val petFactory = PetFactory()
    val pet = petFactory.buildPet(data["petType"].toString())

    pet.name = data["name"] as String
    pet.birthDate = data["birthDate"] as LocalDate
    pet.description = data["description"] as String
    pet.pictures = data["pictures"] as List<String>
    pet.owner = data["owner"] as String

    return pet
}

fun storePetToDatabase(pet: Pet) {

}