package com.android.sample

import com.android.PetPamper.model.GroomerReviews
import kotlin.math.pow
import org.junit.Assert.assertEquals
import org.junit.Test

class GroomerReviewTest {
  @Test
  fun testDefault() {
    val gr = GroomerReviews()
    assertEquals("", gr.email)
    assertEquals(0.0, gr.rating, 10.0.pow(-10.0))
    assertEquals(0, gr.reviewCount)
  }
}
