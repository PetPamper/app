package com.android.PetPamper.model

import java.time.LocalDate

enum class PetType(val petType: String) {
  DEFAULT("default"),
  DOG("dog"),
}

sealed class Pet(
    protected open val _id: String = "",
    protected open var _petType: PetType = PetType.DEFAULT,
    protected open var _name: String = "Unnamed",
    protected open var _birthDate: LocalDate = LocalDate.of(0, 1, 1),
    protected open var _description: String = "",
    protected open var _pictures: List<String> = listOf(""),
    protected open var _ownerId: String = "PetPamper"
) {
  val id
    get() = _id

  var petType
    get() = _petType
    set(value) {
      _petType = value
    }

  var name
    get() = _name
    set(value) {
      _name = value
    }

  var birthDate
    get() = _birthDate
    set(value) {
      if (!value.isAfter(LocalDate.now())) _birthDate = value
    }

  var description
    get() = _description
    set(value) {
      _description = value
    }

  var pictures
    get() = _pictures
    set(value) {
      _pictures = value
    }

  var ownerId
    get() = _ownerId
    set(value) {
      _ownerId = value
    }

  fun addPic(value: String) {
    _pictures = _pictures + value
  }
}

class DefaultPet(
    id: String = "",
    name: String = "Unnamed",
    birthDate: LocalDate = LocalDate.of(0, 1, 1),
    description: String = "",
    pictures: List<String> = listOf(),
    ownerId: String = "PetPamper",
) : Pet(id, PetType.DEFAULT, name, birthDate, description, pictures, ownerId) {}

class Dog(
    id: String = "",
    name: String = "Unnamed",
    birthDate: LocalDate = LocalDate.of(0, 1, 1),
    description: String = "",
    pictures: List<String> = listOf(""),
    ownerId: String = "PetPamper",
    var requiresPermit: Boolean = false
) : Pet(id, PetType.DOG, name, birthDate, description, pictures, ownerId) {}

class PetFactory() {

  private var pet: Pet? = null

  fun buildPet(
      id: String,
      petType: String,
      name: String = "Unnamed",
      birthDate: LocalDate = LocalDate.of(0, 1, 1),
      description: String = "",
      pictures: List<String> = listOf(),
      ownerId: String = "PetPamper"
  ): Pet {
      pet = when (petType) {
          "dog" -> Dog(id = id)
          else -> DefaultPet(id = id)
      }

    val pet: Pet = pet!!

    pet.name = name
    pet.birthDate = birthDate
    pet.description = description
    pet.pictures = pictures
    pet.ownerId = ownerId

    return pet
  }
}
