package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroomerRegisterScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroomerRegisterScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GroomerRegisterScreen") }) {

  val arrowButton: KNode = child { hasTestTag("arrowButton") }
  val errorText: KNode = child { hasTestTag("errorText") }
  val inputText: KNode = child { hasTestTag("InputText") }
  val displayText: KNode = child { hasTestTag("DisplayText") }

  fun field(fieldName: String, i: Int): KNode {
    return child { hasTestTag("$fieldName$i") }
  }

  fun inputText(i: Int): KNode {
    return field("InputText", i)
  }

  fun checkbox(i: Int): KNode {
    return field("Checkbox", i)
  }
}
