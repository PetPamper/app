package com.android.PetPamper.ui.screen.register

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.User

class GroomerSignUpViewModel {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var password by mutableStateOf("")
    var address by mutableStateOf(Address("", "", "", ""))
    var experienceYears by mutableStateOf("")
    var groomerServices = mutableStateListOf<String>()
}

const val NUM_STEPS = 9

@Composable
fun GroomerRegister(viewModel: GroomerSignUpViewModel, navController: NavController) {
    var currentStep by remember { mutableIntStateOf(1) }

    when (currentStep) {
        1 ->
            GroomerRegisterLayout(
                1,
                "Let’s start with your name",
                "Name",
                isValidInput = ::isValidName,
                errorText = "Please enter a valid name.",
                onNext = { newName ->
                    viewModel.name = newName
                    currentStep++
                })
        2 ->
            GroomerRegisterLayout(
                2,
                "Hello ${viewModel.name}, enter your email",
                "Email",
                isValidInput = ::isValidEmail,
                errorText = "Please enter a valid email.",
                onNext = { newEmail ->
                    viewModel.email = newEmail
                    currentStep++
                })
        3 ->
            GroomerRegisterLayout(
                3,
                "What’s your phone number?",
                "Phone Number",
                onNext = { newPhoneNumber ->
                    viewModel.phoneNumber = newPhoneNumber
                    currentStep++
                })
        4 ->
            GroomerRegisterLayout(
                4,
                "Great! Create your password",
                "Password",
                isValidInput = ::isValidPassword,
                errorText = "Password must be at least 8 characters.",
                onNext = { password ->
                    viewModel.password = password
                    currentStep++
                })
        5 -> {
            GroomerRegisterLayout(
                5,
                "Confirm your password",
                "Confirm Password",
                isValidInput = { confirmedPassword -> confirmedPassword == viewModel.password},
                errorText = "Passwords do not match.",
                onNext = { _ ->
                    currentStep++
                })
        }
        6 ->
            GroomerRegisterMultipleLayout(
                6,
                "Enter your address",
                listOf("Street", "City", "State", "Postal Code"),
                onNext = { fieldsList ->
                    viewModel.address.city = fieldsList[0]
                    viewModel.address.state = fieldsList[1]
                    viewModel.address.street = fieldsList[2]
                    viewModel.address.postalCode = fieldsList[3]
                    currentStep++
                })
        7 ->
            GroomerRegisterLayout(
                7,
                "How many years of experience do you have as a groomer?",
                "Experience Years",
                onNext = { experienceYears ->
                    viewModel.experienceYears = experienceYears
                    currentStep++
                })
        8 ->
            GroomerRegisterCheckboxLayout(
                8,
                "What types of services do you provide?",
                listOf("Bath", "Brushing", "Eye/ear cleaning", "Hair trimming",
                    "Nail trimming", "Teeth brushing", "De-shedding", "Dematting"),
                onNext = { groomerServices ->
                    for (service in groomerServices)
                    {
                        viewModel.groomerServices.add(service)
                    }
                    currentStep++
                })
//        9 -> {
//            var checkServices = ""
//            for (service in viewModel.groomerServices)
//            {
//                checkServices += "$service "
//            }
//            GroomerRegisterLayout(
//                currentStep = 7,
//                textShown = checkServices,
//                fieldName = "test",
//                onNext = {_ ->
//                    currentStep++
//                })
//        999 -> {
//            GroomerRegisterLayout(
//                6,
//                false,
//                "Confirm your password",
//                "Confirm Password",
//                confirmPassword = viewModel.password,
//                onNext = { confirmedPassword ->
//                    if (viewModel.password == confirmedPassword) {
//
//                        val firebaseConnection = FirebaseConnection()
//
//                        firebaseConnection.registerUser(
//                            viewModel.email,
//                            viewModel.password,
//                            onSuccess = {
//                                firebaseConnection.addUser(
//                                    User(
//                                        viewModel.name,
//                                        viewModel.email,
//                                        viewModel.phoneNumber,
//                                        viewModel.address),
//                                    onSuccess = { currentStep++ },
//                                    onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
//                            },
//                            onFailure = { error -> Log.e("SignUp", "Registration failed", error) })
//                    } else {
//                        // Show error message
//                    }
//                })
//        }
        9 -> navController.navigate("LoginScreen")
    }

    // Add more steps as needed
}

@Composable
fun GroomerRegisterLayout(
    currentStep: Int,
    textShown: String,
    fieldName: String,
    isValidInput: ((String) -> Boolean)? = null,
    errorText: String = "",
    onNext: ((String) -> Unit)? = null
) {

    var textField by remember { mutableStateOf("") }
    var shownErrorText by remember { mutableStateOf("") }

    fun proceedWithNext() {
        var proceed = true

        if (isValidInput?.invoke(textField) == false) {
            println("what")
            shownErrorText = errorText
            proceed = false
        }

        if (proceed) {
            shownErrorText = ""
            onNext?.invoke(textField)
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("GroomerRegisterScreen")) {
        Text(
            text = textShown,
            style =
            TextStyle(
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(800),
                color = Color(0xFF2490DF),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.testTag("DisplayText"))

        var textVisible by remember {
            mutableStateOf(fieldName != "Password" && fieldName != "Confirm Password")
        }
        OutlinedTextField(
            value = textField,
            onValueChange = { textField = it },
            label = { Text(fieldName) },
            singleLine = true,
            keyboardOptions =
            when (fieldName) {
                "Password",
                "Confirm Password" -> KeyboardOptions(keyboardType = KeyboardType.Password)
                "Phone Number" -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                else -> KeyboardOptions.Default.copy(imeAction = ImeAction.Done) },
            visualTransformation =
            if (!textVisible)
                PasswordVisualTransformation()
            else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("InputText"),
            colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor =
                Color(0xFF2491DF), // Border color when the TextField is focused
                focusedLabelColor =
                Color(0xFF2491DF), // Label color when the TextField is focused
                unfocusedBorderColor =
                Color.Gray, // Additional customization for other states
                unfocusedLabelColor = Color.Gray),
            trailingIcon = {
                when (fieldName) {
                    "Password",
                    "Confirm Password" -> {
                        val image =
                            if (textVisible) painterResource(id = R.drawable.baseline_visibility_24)
                            else painterResource(id = R.drawable.baseline_visibility_off_24)
                        val description = if (textVisible) "Hide password" else "Show password"
                        // Icon to toggle password visibility
                        IconButton(onClick = { textVisible = !textVisible }) {
                            Icon(painter = image, contentDescription = description)
                        }
                    }
                    else -> {}
                }
            }
        )

        Text(
            text = shownErrorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .testTag("errorText"))

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { proceedWithNext() },
                    modifier =
                    Modifier
                        .wrapContentWidth()
                        .testTag("arrowButton"), // Make the button wrap its content
                    colors =
                    ButtonDefaults.buttonColors( // Set the button's background color
                        containerColor = Color(0xFF2491DF))) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go forward",
                        tint = Color.White,
                        // Set the icon color to blue
                    )
                }

                Spacer(
                    modifier =
                    Modifier.height(16.dp)) // This adds space between the button and the
                // progress bar

                val progress = currentStep.toFloat() / NUM_STEPS
                LinearProgressIndicator(
                    progress = { progress },
                    color = Color(0xFF2491DF),
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)))
            }
        }
    }
}

@Composable
fun GroomerRegisterMultipleLayout(
    currentStep: Int,
    textShown: String,
    fieldNames: List<String>,
    areValidInputs: ((List<String>) -> (List<Boolean>))? = null,
    errorTexts: List<String>? = null,
    onNext: ((List<String>) -> Unit)? = null,
) {
    val numFields = fieldNames.size
    val textFields = remember { mutableStateListOf<String>() }
    val shownErrorTexts = remember { mutableStateListOf<String>() }

    for (i in 1..numFields)
    {
        textFields.add("")
        shownErrorTexts.add("")
    }

    fun proceedWithNext() {
        var isValidInput = true
        val inputsValid = areValidInputs?.invoke(textFields)

        for (i in 0 until numFields)
        {
            if (inputsValid?.get(i) == false)
            {
                isValidInput = false
                shownErrorTexts[i] = errorTexts?.get(i) ?: ""
            }
        }

        if (isValidInput) {
            for (i in 0 until numFields)
            {
                shownErrorTexts[i] = ""
            }
            onNext?.invoke(textFields)
        }
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("RegisterScreen")) {
        item {
            Text(
                text = textShown,
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.testTag("DisplayText"))
        }
        itemsIndexed(fieldNames) {i, _ ->
            OutlinedTextField(
                value = textFields[i],
                onValueChange = { textFields[i] = it },
                label = { Text(fieldNames[i]) },
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("InputText")
                ,
                colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor =
                    Color(0xFF2491DF), // Border color when the TextField is focused
                    focusedLabelColor =
                    Color(0xFF2491DF), // Label color when the TextField is focused
                    unfocusedBorderColor =
                    Color.Gray, // Additional customization for other states
                    unfocusedLabelColor = Color.Gray))

            Text(
                text = shownErrorTexts[i],
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .testTag("errorText")
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = { proceedWithNext() },
                        modifier =
                        Modifier
                            .wrapContentWidth()
                            .testTag("ArrowButton"), // Make the button wrap its content
                        colors =
                        ButtonDefaults.buttonColors( // Set the button's background color
                            containerColor = Color(0xFF2491DF))) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go forward",
                            tint = Color.White,
                            // Set the icon color to blue
                        )
                    }

                    Spacer(
                        modifier =
                        Modifier.height(16.dp)) // This adds space between the button and the
                    // progress bar

                    val progress = currentStep.toFloat() / NUM_STEPS
                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF2491DF),
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(10.dp)))
                }
            }
        }
    }
}

@Composable
fun GroomerRegisterCheckboxLayout(
    currentStep: Int,
    textShown: String,
    checkboxOptions: List<String>,
    onNext: ((List<String>) -> Unit)? = null
) {
    val numOptions = checkboxOptions.size

    val boxesChecked = remember { mutableStateListOf<Boolean>() }
    val checkedOptions = remember { mutableStateListOf<String>() }

    for (i in 0 until numOptions)
    {
        boxesChecked.add(false)
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("GroomerRegisterScreen")) {
        item {
            Text(
                text = textShown,
                style =
                TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF2490DF),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.testTag("DisplayText"))

            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(checkboxOptions) {i, _ ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("Checkbox")
            ) {
                Checkbox(
                    checked = boxesChecked[i],
                    onCheckedChange = {
                        isChecked -> boxesChecked[i] = isChecked
                    })
                Text(text = checkboxOptions[i])
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = {
                            checkedOptions.clear()
                            for (i in 0 until numOptions)
                            {
                                if (boxesChecked[i])
                                {
                                    checkedOptions.add(checkboxOptions[i])
                                }
                            }
                            onNext?.invoke(checkedOptions) },
                        modifier = Modifier
                            .wrapContentWidth()
                            .testTag("arrowButton"), // Make the button wrap its content
                        colors = ButtonDefaults.buttonColors( // Set the button's background color
                            containerColor = Color(0xFF2491DF))) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go forward",
                            tint = Color.White,
                            // Set the icon color to blue
                        )
                    }

                    Spacer(
                        modifier =
                        Modifier.height(16.dp)) // This adds space between the button and the
                    // progress bar

                    val progress = currentStep.toFloat() / NUM_STEPS
                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF2491DF),
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(10.dp)))
                }
            }
        }
    }
}

@Preview
@Composable
fun GroomerRegisterPreview() {
    val viewModel = remember { GroomerSignUpViewModel() }
    val navController = rememberNavController()
    GroomerRegister(viewModel, navController)
}
