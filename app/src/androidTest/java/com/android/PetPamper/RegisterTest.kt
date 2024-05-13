package com.android.PetPamper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.PetPamper.screen.MainScreen
import com.android.PetPamper.screen.RegisterScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterTest : TestCase() {

  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before fun setUp() {}

  @Test
  fun testRegister() = run {
    step("Attempt to register") {
      ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
        registerButton { performClick() }
      }

      // Now that navigation should have occurred, proceed with the RegisterScreen actions.
      ComposeScreen.onComposeScreen<RegisterScreen>(composeTestRule) {
        fun testStep(lblTxt: String, errTxt: String, inTxt: String) {
          errorText { assertIsNotDisplayed() }
          inputLabel {
            assertIsDisplayed()
            assertTextEquals(lblTxt)
          }
          arrowButton { performClick() }
          errorText {
            assertIsDisplayed()
            assertTextEquals(errTxt)
          }
          inputText { performTextInput(inTxt) }
          arrowButton { performClick() }
        }
        testStep("Let’s start with your name", "Please enter a valid name.", "John")
        testStep(
            "Hello John, enter your email", "Please enter a valid email.", "alikawazaki@gmail.com")
        testStep("What’s your phone number?", "Please enter a valid phone number.", "0782074677")

        errorText { assertIsNotDisplayed() }
        inputLabel {
          assertIsDisplayed()
          assertTextEquals("Enter your Address?")
        }

        inputText { performTextInput("Route des toches 7") }

        cityTag { performTextInput("Lausanne") }

        stateTag { performTextInput("Switzerland") }

        postalTag { performTextInput("1026") }

        arrowButton { performClick() }

        testStep(
            "Great! Create your password", "Password must be at least 8 characters.", "12345678")
        testStep("Confirm your password", "Passwords do not match.", "12345678")
      }
    }
  }
}
