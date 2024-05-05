package com.android.PetPamper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.android.PetPamper.screen.GroomerRegisterScreen
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
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

  @Before fun setUp() {}

  @Test
  fun testGroomerRegister() = run {
    step("Attempt to register as groomer") {
      ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
        groomerToggle { performClick() }
        registerButton { performClick() }
      }
    }

    ComposeScreen.onComposeScreen<GroomerRegisterScreen>(composeTestRule) {
      fun testSimpleLayout(lblTxt: String, inTxt: String) {
        displayText {
          assertIsDisplayed()
          assertTextEquals(lblTxt)
        }
        inputText { performTextInput(inTxt) }
        arrowButton { performClick() }
      }
      fun testSimpleLayoutWithError(lblTxt: String, errTxt: String, inTxt: String) {
        errorText { assertIsNotDisplayed() }
        arrowButton { performClick() }
        errorText {
          assertIsDisplayed()
          assertTextEquals(errTxt)
        }
        testSimpleLayout(lblTxt, inTxt)
      }

      fun testMultipleLayout(lblTxt: String, inTxt: List<String>) {
        val n = inTxt.size
        displayText {
          assertIsDisplayed()
          assertTextEquals(lblTxt)
        }

        for (i in 0 until n) {
          val inputText = inputText(i)
          inputText { performTextInput(inTxt[i]) }
        }
        arrowButton { performClick() }
      }

      fun testCheckboxLayout(lblTxt: String, checks: List<Int>) {
        displayText {
          assertIsDisplayed()
          assertTextEquals(lblTxt)
        }

        for (check in checks) {
          val checkbox = checkbox(check)
          checkbox { performClick() }
        }
        arrowButton { performClick() }
      }

      testSimpleLayoutWithError(
          "Let’s start with your name", "Please enter a valid name.", "Groomer")
      //      testSimpleLayoutWithError(
      //          "Hello Groomer, enter your email", "Please enter a valid email.",
      // "groomer@test.test")
      //      testSimpleLayoutWithError(
      //          "What’s your phone number?", "Please enter a valid phone number.", "0100000000")
      //      testSimpleLayoutWithError(
      //          "Great! Create your password", "Password must be at least 8 characters.",
      // "12345678")
      //      testSimpleLayoutWithError("Confirm your password", "Passwords do not match.",
      // "12345678")
      //
      //            testMultipleLayout("Enter your address", listOf("Champ de Mars", "Paris",
      // "France",
      //       "75007"))
      //
      //            testSimpleLayout("How many years of experience do you have as a groomer?", "5")
      //            testSimpleLayout("What is your average service price for an hour", "50")
      //
      //            testCheckboxLayout("What types of services do you provide?", listOf(0, 1, 3))
      //            testCheckboxLayout("What types of pets do you groom?", listOf(1, 4, 5))
      //
      //            arrowButton { performClick() }
    }
  }

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
