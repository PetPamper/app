package com.android.sample

import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.LocationMap
import org.junit.Assert.assertEquals
import org.junit.Test

class GroomerTest {
  @Test
  fun testDefaults() {
    val g = Groomer()
    assertEquals("", g.name)
    assertEquals("", g.email)
    assertEquals("", g.phoneNumber)
    assertEquals(Address("", "", "", "", LocationMap(0.0, 0.0, "Default Location")), g.address)
    assertEquals("", g.yearsExperience)
    assertEquals(listOf(""), g.services)
    assertEquals(listOf(""), g.petTypes)
    assertEquals("", g.profilePic)
    assertEquals(0, g.price)
  }
}
