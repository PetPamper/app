package com.android.PetPamper.ui.screen.users

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Pet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PetListViewModel(email: String, private val petDataHandler: PetDataHandler) : ViewModel() {
    private var _petsList: List<Pet> = listOf()
    val petsList: List<Pet>
        get() = _petsList

    init {
        runBlocking {
            launch {
                _petsList = petDataHandler.retrievePetsFromOwner(email) ?: run {
                    Log.e("PetListScreenViewModel", "Could not retrieve pets")
                    listOf()
                }
            }
        }
    }
}