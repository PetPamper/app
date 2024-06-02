package com.android.PetPamper.ui.screen.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.PetPamper.ui.screen.users.CustomTextButton

@Composable
fun LoginComponent(viewModel: LoginViewModel) {

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = viewModel.topText,
            style =
            TextStyle(
                fontSize = 23.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(800),
                color = Color(0xFF2490DF),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.testTag("TopText"))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text(viewModel.emailLabel) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text(viewModel.passwordLabel) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(15.dp))

        if (viewModel.showError) {
            Text(
                text = viewModel.errorMessage,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ErrorMessage"))

            Spacer(modifier = Modifier.height(4.dp))
        }

        CustomTextButton(viewModel.forgotPasswordText, "", "forgetButton") {
            viewModel.onForgotPassword()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onCLick()
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF2491DF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("LoginButton")) {
            Text("LOG IN", fontSize = 18.sp)
        }
    }
}