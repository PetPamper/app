package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RegisterScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RegisterScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("RegisterScreen") }) {

  val arrowButton: KNode = child { hasTestTag("arrowButton") }
  val errorText: KNode = child { hasTestTag("errorText") }
  val inputText: KNode = child { hasTestTag("inputText") }
  val cityTag: KNode = child { hasTestTag("cityTag") }
  val stateTag: KNode = child { hasTestTag("stateTag") }
  val postalTag: KNode = child { hasTestTag("postalTag") }
  val inputLabel: KNode = child { hasTestTag("inputLabel") }
}
