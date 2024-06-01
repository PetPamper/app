package com.android.PetPamper.ui.screen.users

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Pet

class PetListViewModel(email: String, private val petDataHandler: PetDataHandler) : ViewModel() {
  private val _petsList: MutableList<Pet> = mutableStateListOf()
  val petsList: List<Pet>
    get() = _petsList

  init {
    petDataHandler.retrievePetsFromOwner(email, _petsList)
  }
}
