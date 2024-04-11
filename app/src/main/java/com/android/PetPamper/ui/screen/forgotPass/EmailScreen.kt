package com.android.PetPamper.ui.screen.forgotPass

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection


class EmailViewModel() {

    var email by mutableStateOf("")


}

@Composable
fun EmailScreen(viewModel: EmailViewModel, navController: NavController) {
    var currentStep by remember { mutableStateOf(1) }



    when (currentStep) {
        1 ->
            EmailScreenLayout("Enter your email" , "Email", viewModel) { email ->
                viewModel.email = email
                if(email.isNotBlank()){
                    val firebaseConnection = FirebaseConnection()
                    if(firebaseConnection.restPasswordSendEmail(email)){
                        print("Email sent")
                    }

                    currentStep++
                }
            }


        2 ->
            EmailSentSuccessScreen(navController, viewModel.email.isNotBlank())

        3 ->
            navController.navigate("LoginScreen")
    }



}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailScreenLayout(textShown: String, fieldname: String, viewModel: EmailViewModel, onNext: (String) -> Unit){
    val keyboardController = LocalSoftwareKeyboardController.current
    var textField by remember { mutableStateOf("") }

    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = textShown,
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = textField,
                onValueChange = { textField = it },
                label = { Text(fieldname) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier.fillMaxWidth(),
                colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor = Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { onNext(textField) },
                    modifier = Modifier.wrapContentWidth(), // Make the button wrap its content
                    colors =
                    ButtonDefaults.buttonColors( // Set the button's background color
                        containerColor = Color(0xFF2491DF))) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Go forward",
                        tint = Color.White // Set the icon color to blue
                    )
                }

                Spacer(
                    modifier =
                    Modifier.height(
                        16.dp)) // This adds space between the button and the progress bar

                LinearProgressIndicator(
                    progress = 0.2f,
                    color = Color(0xFF2491DF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)))
            }
        }
    }

}




@Composable
fun EmailSentSuccessScreen(navController: NavController, isEmailBlank: Boolean){
    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp))  {
        val maxHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

            Text(
                text = "Email sent successfully",
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ))
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(id = R.drawable.check_success),
                contentDescription = "Succuss Icon",
                modifier = Modifier.size(120.dp).clip(CircleShape))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End) {
                Button(
                    onClick = {
                            navController.navigate("LoginScreen")
                          },
                    modifier = Modifier.wrapContentWidth(), // Make the button wrap its content
                    colors =
                    ButtonDefaults.buttonColors( // Set the button's background color
                        containerColor = Color(0xFF2491DF))) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Go forward",
                        tint = Color.White // Set the icon color to blue
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun EmailScreenPreview() {
    val viewModel = remember { EmailViewModel() } // In actual app, provide this via ViewModel
    val navController  = rememberNavController()
    EmailScreen(viewModel, navController)
}
