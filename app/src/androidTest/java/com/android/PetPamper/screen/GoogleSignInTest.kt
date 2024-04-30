package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GoogleSignInTest(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GoogleSignInTest>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GoogleSignInTest") }) {

  val name: KNode = child { hasTestTag("TextShownTag") }
  val valueWritten: KNode = child { hasTestTag("valueWritten") }
  val ForwardButton: KNode = child { hasTestTag("ForwardButton") }


  val city: KNode = child { hasTestTag("cityTag") }
  val state: KNode = child { hasTestTag("stateTag") }
  val postalCode: KNode = child { hasTestTag("postalTag") }

  // Add this line

}
