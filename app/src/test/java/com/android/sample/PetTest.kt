package com.android.sample

import com.android.PetPamper.Dog
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class PetTest {

  @Test
  fun checkDefaults() {
    val pet = Dog()
    assertEquals("Unnamed", pet.name)
    assertEquals(LocalDate.of(0,1,1), pet.birthDate)
    assertEquals("",pet.description)
    assertEquals(listOf(""),pet.pictures)
  }

  @Test
  fun checkCreate() {
    val pet = Dog(birthDate = LocalDate.of(2024,3,28),name = "PetPamper's Mascot", pictures = listOf("mascot1.png","mascot2.jpg"), description = "This is PetPamper's Mascot, it currently has a placeholder description!", requiresPermit = true)
    assertEquals("PetPamper's Mascot", pet.name)
    assertEquals(LocalDate.of(2024,3,28), pet.birthDate)
    assertEquals("This is PetPamper's Mascot, it currently has a placeholder description!",pet.description)
    assertEquals(listOf("mascot1.png","mascot2.jpg"),pet.pictures)
    assertEquals("PetPamper",pet.owner)
    assertEquals(true,pet.requiresPermit)
  }

  @Test
  fun checkAddPic() {
    val pet = Dog(name = "PetPamper's Mascot", pictures = listOf("mascot1.png","mascot2.jpg"), description = "This is PetPamper's Mascot, it currently has a placeholder description!", requiresPermit = true)
    assertEquals("PetPamper's Mascot", pet.name)
    assertEquals("This is PetPamper's Mascot, it currently has a placeholder description!",pet.description)
    assertEquals(listOf("mascot1.png","mascot2.jpg"),pet.pictures)
    assertEquals(true,pet.requiresPermit)
    pet.addPic("mascot3.jpeg")
    assertEquals(listOf("mascot1.png","mascot2.jpg","mascot3.jpeg"),pet.pictures)
  }
}
