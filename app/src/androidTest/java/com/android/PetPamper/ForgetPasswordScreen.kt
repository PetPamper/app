package com.android.PetPamper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.PetPamper.screen.ForgetPassword
import com.android.PetPamper.screen.MainScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgetPasswordScreen : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before fun setUp() {}

  @Test
  fun testRegister() = run {
    step("Attempt to forget") {
      ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
        forgetPasswordButton { performClick() }
      }

      // Now that navigation should have occurred, proceed with the RegisterScreen actions.
      ComposeScreen.onComposeScreen<ForgetPassword>(composeTestRule) {
        emailForgot {
          assertIsDisplayed()
          assertTextEquals("Enter your email")
        }

        emailfield { performTextInput("alitennis131800@gmail.com") }

        FinishButton { performClick() }

        SuccessfullMessage {
          assertIsDisplayed()
          assertTextEquals("Email sent successfully")
        }
      }
    }
  }
}
