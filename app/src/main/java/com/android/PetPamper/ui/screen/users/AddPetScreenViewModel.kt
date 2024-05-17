package com.android.PetPamper.ui.screen.users

import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.PetDataHandler

class AddPetScreenViewModel(val email: String, val petDataHandler: PetDataHandler) : ViewModel() {}
