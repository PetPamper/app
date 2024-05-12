package com.android.sample

import com.android.PetPamper.model.Dog
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class PetTest {

  @Test
  fun checkDefaults() {
    val pet = Dog()
    assertEquals("", pet.id)
    assertEquals("Unnamed", pet.name)
    assertEquals(LocalDate.of(0, 1, 1), pet.birthDate)
    assertEquals("", pet.description)
    assertEquals(listOf(""), pet.pictures)
  }

  @Test
  fun checkCreate() {
    val pet =
        Dog(
            id = "123",
            birthDate = LocalDate.of(2024, 3, 28),
            name = "PetPamper's Mascot",
            pictures = listOf("mascot1.png", "mascot2.jpg"),
            description = "This is PetPamper's Mascot, it currently has a placeholder description!",
            requiresPermit = true)
    assertEquals("123", pet.id)
    assertEquals("PetPamper's Mascot", pet.name)
    assertEquals(LocalDate.of(2024, 3, 28), pet.birthDate)
    assertEquals(
        "This is PetPamper's Mascot, it currently has a placeholder description!", pet.description)
    assertEquals(listOf("mascot1.png", "mascot2.jpg"), pet.pictures)
    assertEquals("PetPamper", pet.ownerId)
    assertEquals(true, pet.requiresPermit)
  }

  @Test
  fun checkAddPic() {
    val pet =
        Dog(
            name = "PetPamper's Mascot",
            pictures = listOf("mascot1.png", "mascot2.jpg"),
            description = "This is PetPamper's Mascot, it currently has a placeholder description!",
            requiresPermit = true)
    assertEquals("PetPamper's Mascot", pet.name)
    assertEquals(
        "This is PetPamper's Mascot, it currently has a placeholder description!", pet.description)
    assertEquals(listOf("mascot1.png", "mascot2.jpg"), pet.pictures)
    assertEquals(true, pet.requiresPermit)
    pet.addPic("mascot3.jpeg")
    assertEquals(listOf("mascot1.png", "mascot2.jpg", "mascot3.jpeg"), pet.pictures)
  }

  @Test
  fun checkChangeValues() {
    val dog = Dog()

    assertEquals("Unnamed", dog.name)
    assertEquals(LocalDate.of(0, 1, 1), dog.birthDate)
    assertEquals("", dog.description)
    assertEquals(listOf(""), dog.pictures)
    assertEquals(false, dog.requiresPermit)

    dog.name = "PetPamper's Mascot"
    dog.birthDate = LocalDate.of(2024, 3, 28)
    dog.description = "Lorem ipsum"
    dog.pictures = listOf("mascot1.png", "mascot2.jpg")
    dog.ownerId = "PetPamper Team"
    dog.requiresPermit = true

    assertEquals("PetPamper's Mascot", dog.name)
    assertEquals(LocalDate.of(2024, 3, 28), dog.birthDate)
    assertEquals("Lorem ipsum", dog.description)
    assertEquals(listOf("mascot1.png", "mascot2.jpg"), dog.pictures)
    assertEquals("PetPamper Team", dog.ownerId)
    assertEquals(true, dog.requiresPermit)

    dog.addPic("mascot3.jpeg")
    assertEquals(listOf("mascot1.png", "mascot2.jpg", "mascot3.jpeg"), dog.pictures)

    dog.birthDate = LocalDate.of(2026, 3, 28)
    assertEquals(LocalDate.of(2024, 3, 28), dog.birthDate)
  }
}
