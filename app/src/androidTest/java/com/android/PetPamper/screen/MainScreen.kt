package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class MainScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MainScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }
  val errorMessage: KNode = child { hasTestTag("ErrorMessage") } // Add this line
  val googleSignInButton: KNode = child { hasTestTag("googleSignInButton") }
  val registerButton: KNode = child { hasTestTag("registerButton") }
  val forgetPasswordButton: KNode = child { hasTestTag("forgetButton") }
}
