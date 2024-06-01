package com.android.PetPamper.ui.screen.users

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.ViewModel
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Pet
import com.android.PetPamper.model.PetFactory
import com.android.PetPamper.model.PetType
import com.google.firebase.storage.FirebaseStorage
import java.time.DateTimeException
import java.time.LocalDate

enum class FieldType() {
  OUTLINED_TEXT_FIELD,
  DROPDOWN_MENU,
  GALLERY_IMAGE_PICKER,
}

class AddPetScreenViewModel(val email: String, val petDataHandler: PetDataHandler) : ViewModel() {

  val fieldNames = listOf("Name", "Pet type", "Birth date (dd/mm/yyyy)", "Description", "Pictures")
  val fieldTypes =
      listOf(
          FieldType.OUTLINED_TEXT_FIELD,
          FieldType.DROPDOWN_MENU,
          FieldType.OUTLINED_TEXT_FIELD,
          FieldType.OUTLINED_TEXT_FIELD,
          FieldType.GALLERY_IMAGE_PICKER)
  val visualTransformations: Map<Int, VisualTransformation> = mapOf(2 to DateTransformation())

  val dropDownOptions: Map<Int, List<String>> =
      mapOf((1 to PetType.values().map { petType -> petType.petType }))
  val dropDownExtended = mutableStateMapOf(1 to false)

  val fieldShownVals = mutableStateListOf("", "dog", "", "", "")
  // private val fieldVals = mutableStateListOf("", "", "", "", "")
  val errorTexts = mutableStateListOf("", "", "", "", "", "")
  val errorShown = mutableStateListOf(false, false, false, false, false, false)

  val addPetError = "Could not register pet"
  var goToPreviousScreen by mutableStateOf(false)

  // val addPetEnabled = mutableStateOf(true)

  /** Function that handles input changes for each field */
  fun handleInput(fieldIndex: Int, input: String) {
    when (fieldIndex) {
      0,
      1,
      3 -> fieldShownVals[fieldIndex] = input
      2 -> fieldShownVals[fieldIndex] = if (input.length >= 8) input.substring(0, 8) else input
      4 -> {
        val uri = Uri.parse(input)
        var imageUri: Uri? = null

        if (uri != null) {
          // Get a reference to the storage service
          val storageRef = FirebaseStorage.getInstance().reference

          val fileRef = storageRef.child("images/${uri.lastPathSegment}")
          val uploadTask = fileRef.putFile(uri)

          uploadTask
              .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                  imageUri = downloadUri // Store download URI instead of local URI
                  fieldShownVals[fieldIndex] = downloadUri.toString()
                }
              }
              .addOnFailureListener {
                // Handle unsuccessful uploads
                errorTexts[fieldIndex] = "Upload failed: ${it.message}"
              }
        } else {
          fieldShownVals[fieldIndex] = ""
        }
      }
    }
  }

  private fun clearErrorTexts() {
    for (i in 0 until errorTexts.size) {
      errorTexts[i] = ""
      errorShown[i] = false
    }
  }

  /** Function to add pet to the database from input values */
  fun onAddPet(errorHandler: (Exception) -> Unit = {}) {
    clearErrorTexts()

    val petFactory = PetFactory()

    val pet =
        petFactory.buildPet(
            id = petDataHandler.generateNewId(),
            petType = fieldShownVals[1],
            name = fieldShownVals[0],
            birthDate =
                if (fieldShownVals[2].length < 8) {
                  errorTexts[2] = "Entered date is incorrect"
                  errorShown[2] = true
                  Log.d("AddPetScreenViewModel", "date is too short")
                  return
                } else
                    try {
                      LocalDate.of(
                          fieldShownVals[2].substring(4, 8).toInt(),
                          fieldShownVals[2].substring(2, 4).toInt(),
                          fieldShownVals[2].substring(0, 2).toInt())
                    } catch (e: Exception) {
                      when (e) {
                        is NumberFormatException -> {
                          errorTexts[2] = "Entered date is incorrect"
                          errorShown[2] = true
                          Log.d("AddPetScreenViewModel", "date wasn't numbers")
                          return
                        }
                        is DateTimeException -> {
                          errorTexts[2] = "Entered date is incorrect"
                          errorShown[2] = true
                          Log.d("AddPetScreenViewModel", "date wasn't correct format")
                          return
                        }
                        else -> throw e
                      }
                    },
            description = fieldShownVals[3],
            pictures = if (fieldShownVals[4] != "") listOf(fieldShownVals[4]) else listOf(),
            ownerId = email)

    addPet(pet, errorHandler)
  }

  /**
   * Function to add a pet to the database
   *
   * @param pet pet to add to the database
   * @param errorHandler function to call in case of error
   */
  private fun addPet(pet: Pet, errorHandler: (Exception) -> Unit = {}) {
    val finalErrorHandler: (Exception) -> Unit = { exception ->
      errorTexts[errorTexts.size - 1] = "Could not register pet"
      errorShown[errorShown.size - 1] = true
      errorHandler(exception)
      Log.d("AddPetScreenViewModel", "couldn't store pet")
    }
    petDataHandler.storePetToDatabase(pet, errorHandler = finalErrorHandler)
    goToPreviousScreen = true
  }
}

/** Visual transformation allowing dates to be displayed in the format dd/mm/yyyya */
class DateTransformation : VisualTransformation {

  // dd/mm/yyyy format
  override fun filter(text: AnnotatedString): TransformedText {
    var out = ""
    text.text.forEachIndexed { index, char ->
      when (index) {
        2 -> out += "/$char"
        4 -> out += "/$char"
        else -> out += char
      }
    }
    val numberOffsetTranslator =
        object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 4) return offset + 1
            return offset + 2
          }

          override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 5) return offset - 1
            return offset - 2
          }
        }
    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
  }
}
