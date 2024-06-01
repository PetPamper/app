package com.android.PetPamper.ui.screen.users

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Pet
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PetListViewModel(email: String, private val petDataHandler: PetDataHandler) : ViewModel() {
  private val _petsList: MutableList<Pet> = mutableStateListOf()
  val petsList: List<Pet>
    get() = _petsList

  init {
      petDataHandler.retrievePetsFromOwner(email, _petsList)
  }
}
