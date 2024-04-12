package com.android.PetPamper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        arrowButton { performClick() }
        errorText {
          assertIsDisplayed()
          assertTextEquals("Please enter a valid name.")
        }
        NameTextInput { performTextInput("John") }
        arrowButton { performClick() }

        EmailText {
          assertIsDisplayed()
          assertTextEquals("Hello John, enter your email")
        }

        NameTextInput { performTextInput("alikawazaki@gmail.com") }

        arrowButton { performClick() }

        EmailText {
          assertIsDisplayed()
          assertTextEquals("What’s your phone number?")
        }

        NameTextInput { performTextInput("0782074677") }

        arrowButton { performClick() }

        EmailText {
          assertIsDisplayed()
          assertTextEquals("Enter your Address?")
        }

        NameTextInput { performTextInput("Route des toches 7") }

        cityTag { performTextInput("Lausanne") }

        stateTag { performTextInput("Switzerland") }

        postalTag { performTextInput("1026") }

        arrowButton { performClick() }

        EmailText {
          assertIsDisplayed()
          assertTextEquals("Great! Create your password")
        }

        NameTextInput { performTextInput("12345678") }

        arrowButton { performClick() }

        EmailText {
          assertIsDisplayed()
          assertTextEquals("Confirm your password")
        }

        NameTextInput { performTextInput("12345678") }
      }
    }
  }
}
