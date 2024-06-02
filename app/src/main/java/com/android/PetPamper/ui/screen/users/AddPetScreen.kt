package com.android.PetPamper.ui.screen.users

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.PetPamper.ui.screen.register.GalleryImagePicker

@Composable
fun AddPetScreen(viewModel: AddPetScreenViewModel, onBackPressed: () -> Unit) {

  val errorToast = Toast.makeText(LocalContext.current, viewModel.addPetError, Toast.LENGTH_SHORT)

  LaunchedEffect(key1 = viewModel.goToPreviousScreen) {
    if (viewModel.goToPreviousScreen) {
      onBackPressed()
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { androidx.compose.material.Text("Add a pet") },
            navigationIcon = {
              IconButton(onClick = onBackPressed) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            })
      },
  ) { padding ->
    LazyColumn(
        modifier = Modifier.padding(padding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      item { Spacer(modifier = Modifier.height(8.dp)) }

      items(5) { fieldIndex ->
        Row(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
          when (viewModel.fieldTypes[fieldIndex]) {
            FieldType.OUTLINED_TEXT_FIELD -> {
              OutlinedTextField(
                  value = viewModel.fieldShownVals[fieldIndex],
                  onValueChange = { viewModel.handleInput(fieldIndex, it) },
                  label = { Text(viewModel.fieldNames[fieldIndex]) },
                  visualTransformation =
                      viewModel.visualTransformations.getOrElse(fieldIndex) {
                        VisualTransformation.None
                      })
            }
            FieldType.DROPDOWN_MENU -> {
              OutlinedTextField(
                  value = viewModel.fieldShownVals[fieldIndex],
                  onValueChange = {},
                  label = { Text(viewModel.fieldNames[fieldIndex]) },
                  readOnly = true,
                  trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        "",
                        Modifier.clickable {
                          viewModel.dropDownExtended[fieldIndex] =
                              !viewModel.dropDownExtended[fieldIndex]!!
                        })
                  })
              DropdownMenu(
                  expanded = viewModel.dropDownExtended[fieldIndex]!!,
                  onDismissRequest = { viewModel.dropDownExtended[fieldIndex] = false }) {
                    viewModel.dropDownOptions[fieldIndex]!!.forEach { option ->
                      DropdownMenuItem(
                          onClick = {
                            viewModel.handleInput(fieldIndex, option)
                            viewModel.dropDownExtended[fieldIndex] = false
                          }) {
                            Text(option)
                          }
                    }
                  }
            }
            FieldType.GALLERY_IMAGE_PICKER -> {
              GalleryImagePicker { uri -> viewModel.handleInput(fieldIndex, uri.toString()) }
            }
          }
        }

        Text(
            text = viewModel.errorTexts[fieldIndex],
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
      }

      item {
        Spacer(modifier = Modifier.height(50.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Button(
              onClick = { viewModel.onAddPet { errorToast.show() } },
              // enabled = viewModel.addPetEnabled.value,
              shape = RoundedCornerShape(50.dp)) {
                Text(
                    text = "Register pet",
                    color = Color.White,
                    style =
                        TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(600)),
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp))
              }
        }
      }
    }
  }
}
