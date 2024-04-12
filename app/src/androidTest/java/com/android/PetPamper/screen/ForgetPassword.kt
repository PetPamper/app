package com.android.PetPamper.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ForgetPassword(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ForgetPassword>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ForgetPassword") }) {

  val emailForgot: KNode = child { hasTestTag("emailForgot") }
  val emailfield: KNode = child { hasTestTag("emailfield") }
  val SuccessfullMessage: KNode = child { hasTestTag("SuccessfullMessage") }
  val FinishButton: KNode = child { hasTestTag("FinishButton") }
}
