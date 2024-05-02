package com.android.PetPamper

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.screen.GoogleSignInScreen
import com.android.PetPamper.ui.screen.register.SignUpScreenGoogle
import com.android.PetPamper.ui.screen.register.SignUpViewModelGoogle
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoogleSignInTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      SignUpScreenGoogle(SignUpViewModelGoogle(), navController, "alitennis131800@gmail.com")
      val firebaseConnection = FirebaseConnection()
    }
  }

  @Test
  fun testGoogleSignIn() = run {
    step("infos about the google account") {
      ComposeScreen.onComposeScreen<GoogleSignInScreen>(composeTestRule) {
        inputLabel {
          assertIsDisplayed()
          assertTextEquals("Let’s start with your name")
        }

        valueWritten { performTextInput("Ali") }

        ForwardButton { performClick() }

        inputLabel {
          assertIsDisplayed()
          assertTextEquals("What’s your phone number?")
        }

        valueWritten { performTextInput("0782074677") }

        ForwardButton { performClick() }

        inputLabel {
          assertIsDisplayed()
          assertTextEquals("Enter your Address")
        }

        valueWritten { performTextInput("EPFL") }

        city { performTextInput("Lausanne") }

        state { performTextInput("Switzerland") }

        postalCode { performTextInput("1026") }
      }
    }
  }
}
