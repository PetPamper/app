package com.android.PetPamper.model

import java.time.LocalDate

enum class PetType(val petType: String) {
    DOG("dog"),
}
sealed class Pet(
    protected open val _petType: PetType = PetType.DOG,
    protected open var _name: String = "Unnamed",
    protected open var _birthDate: LocalDate = LocalDate.of(0, 1, 1),
    protected open var _description: String = "",
    protected open var _pictures: List<String> = listOf(""),
    protected open var _owner: String = "PetPamper"
) {
    val petType
        get() = _petType

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

    var owner
        get() = _owner
        set(value) {
            _owner = value
        }
  fun addPic(value: String) {
    _pictures = _pictures + value
  }
}

class Dog(
    name: String = "Unnamed",
    birthDate: LocalDate = LocalDate.of(0, 1, 1),
    description: String = "",
    pictures: List<String> = listOf(""),
    owner: String = "PetPamper",
    var requiresPermit: Boolean = false
) : Pet(PetType.DOG, name, birthDate, description, pictures, owner) {

}

class PetFactory() {

    private var pet: Pet? = null
    fun buildPet(petType: String,
                 name: String = "Unnamed",
                 birthDate: LocalDate = LocalDate.of(0, 1, 1),
                 description: String = "",
                 pictures: List<String> = listOf(""),
                 owner: String = "PetPamper"): Pet {
        when(petType) {
            "dog" -> pet = Dog()
            else -> Dog()
        }

        val pet: Pet = pet!!

        pet.name = name
        pet.birthDate = birthDate
        pet.description = description
        pet.pictures = pictures
        pet.owner = owner

        return pet
    }
}