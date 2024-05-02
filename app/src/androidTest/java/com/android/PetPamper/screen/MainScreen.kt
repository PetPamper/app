package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.android.PetPamper.ui.screen.BarScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class MainScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MainScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val emailTbx: KNode = child { hasTestTag("emailTbx") }
  val pwdTbx: KNode = child { hasTestTag("pwdTbx") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }
  val errorMessage: KNode = child { hasTestTag("ErrorMessage") } // Add this line
  val googleSignInButton: KNode = child { hasTestTag("googleSignInButton") }
  val registerButton: KNode = child { hasTestTag("registerButton") }
  val forgetPasswordButton: KNode = child { hasTestTag("forgetButton") }
  val navBarItems: List<KNode> =
      listOf(
              BarScreen.Home,
              BarScreen.Chat,
              BarScreen.Groomers,
              BarScreen.Map,
              BarScreen.Profile,
          )
          .map { screen -> child { hasTestTag(screen.label) } }
  val mainView: KNode = child { hasTestTag("mainScaffold") }
}
