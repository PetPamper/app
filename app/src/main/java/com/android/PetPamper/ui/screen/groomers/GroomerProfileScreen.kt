package com.android.PetPamper.ui.screen.groomers

import com.android.PetPamper.model.GroomerViewModel
import com.android.PetPamper.ui.screen.users.EditProfileDialog


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun GroomerProfileScreen(navController: NavController, groomerVM: GroomerViewModel) {

    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val address = remember { mutableStateOf(Address("", "", "", "", LocationMap())) }
    val isLoading = remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(groomerVM.email) {
        scope.launch {
            val nameDeferred = async { groomerVM.getNameFromFirebase { name.value = it } }
            val phoneDeferred = async { groomerVM.getPhoneNumberFromFirebase { phoneNumber.value = it } }
            val addressDeferred = async { groomerVM.getAddressesFromFirebase { address.value = it } }

            // Wait for all data to be fetched
            nameDeferred.await()
            phoneDeferred.await()
            addressDeferred.await()

            // All data has been fetched, update loading state
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        // Display a loading indicator
        CircularProgressIndicator()
    } else {
        Log.d("UserProfile", "phone: ${phoneNumber.value}")

        var showEditProfile by remember { mutableStateOf(false) }

        var needRecompose by remember { mutableStateOf(false) }

//    if (showEditProfile){
//        EditProfileDialog(
//            onDismiss = {
//                showEditProfile = false
//                needRecompose = true},
//            onSave = {
//                groomerVM.updateUser(name = it.name, email = it.email, phone = it.phoneNumber)
//                showEditProfile = false
//                needRecompose = true},
//            user)
//    }

        LaunchedEffect(groomerVM.email, needRecompose) {
            Log.d("LaunchedEffectTAG", "UserProfileScreen recomposed")
            needRecompose = false
        }

        Log.d("UserProfile1", "phone: ${phoneNumber}")

        Column(modifier = Modifier.padding(12.dp).fillMaxSize()) { // Adding padding around the entire screen content
            // user name
            Text(
                text = name.value,
                style =
                TextStyle(
                    fontSize = 38.sp,
                    lineHeight = 45.6.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF2490DF),
                    // textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp) // Space after the title
            )
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(194.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column() {
                        Image(
                            painter = painterResource(id = R.drawable.user_image),
                            contentDescription = "User profile image",
                            modifier = Modifier.size(width = 100.dp, height = 100.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Button(
                            onClick = { /* Handle your click here */ },
                            modifier =
                            Modifier
                                .width(90.dp) // Adjusted for visual balance
                                .height(40.dp)
                                .padding(start = 25.dp),
                            // Adjusted for visual balance
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            contentPadding =
                            PaddingValues(0.dp) // Ensure there's no padding inside the button
                        ) {
                            Box(
                                contentAlignment =
                                Alignment.Center, // This centers the content inside the Box
                                modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(0.dp) // This makes the Box fill the Button
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(), // Ensure the Row fills the Box
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.editphoto),
                                        contentDescription = "Edit Photo",
                                        modifier = Modifier.size(15.dp),
                                        alignment = Alignment.CenterStart,
                                        contentScale =
                                        ContentScale
                                            .Crop // This ensures the image covers the button area
                                        // without distortion
                                    )
                                    Text(
                                        text = "Edit photo",
                                        style =
                                        TextStyle(
                                            fontSize = 8.sp, // Adjusted for readability
                                            fontWeight = FontWeight.Bold, // Makes text bold
                                            color =
                                            Color.Black // Changed for better visibility against
                                            // likely dark images
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(12.dp), // Adds padding around the entire column content
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement =
                        Arrangement.spacedBy(12.dp) // Adds vertical space between the children
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.phone_symbol),
                            modifier = Modifier
                                .width(21.dp)
                                .height(23.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                        Image(
                            painter = painterResource(id = R.drawable.email),
                            modifier = Modifier
                                .width(16.dp)
                                .height(15.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            modifier = Modifier
                                .width(16.dp)
                                .height(19.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    Text(
                        text =
                        "${phoneNumber.value}\n${groomerVM.email.take(20)}\n${
                            address.value.city.take(
                                20
                            )
                        }",
                        style =
                        TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.W400,
                            color = Color(0xFF11347A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Space between the sections


            Box(
                contentAlignment = Alignment.Center, // Centers the Text over the Button
                modifier =
                Modifier
                    .width(395.dp)
                    .height(43.dp)
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
            ) {
                Button(
                    onClick = { showEditProfile = true },
                    modifier =
                    Modifier.matchParentSize(), // Ensures the Button matches the size of the Box
                    colors =
                    ButtonDefaults.buttonColors(Color.Transparent), // Makes the Button transparent
                    elevation =
                    ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        focusedElevation = 0.dp
                    ) // Removes elevation to avoid shadow
                ) {
                    // Empty content here, as we're overlaying the Text in the Box scope
                }
                Text(
                    text = "Edit profile",
                    style =
                    TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF11347A),
                        textAlign = TextAlign.Center
                    ),
                    modifier =
                    Modifier.align(
                        Alignment.Center
                    ) // Ensures the Text is centered in the Box, over the Button
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Two smaller squares underneath
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Adds padding around the row
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // First square with shadow and rounded corners
                Surface(
                    modifier =
                    Modifier
                        .size(width = 126.dp, height = 129.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                        .padding(4.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFFF4F3F3) // Sets the background color of the Surface
                ) {
                    Column(
                        modifier =
                        Modifier.padding(4.dp), // Adds padding around the entire column content
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement =
                        Arrangement.spacedBy(4.dp) // Adds vertical space between the children
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.wallet),
                            modifier = Modifier
                                .width(88.dp)
                                .height(82.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            text = "My Wallet",
                            style =
                            TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                // fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF758287),
                            )
                        )
                    }
                }
                // Second square with shadow and rounded corners
            }
            Spacer(modifier = Modifier.height(124.dp))
            Box(
                Modifier
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                    .width(395.dp)
                    .height(50.dp)
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Image(
                        modifier = Modifier
                            .width(20.dp)
                            .height(40.dp),
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = "Settings",
                        modifier = Modifier
                            .width(357.dp)
                            .height(65.dp),
                        style =
                        TextStyle(
                            fontSize = 24.sp,
                            // fontFamily = FontFamily(Font(R.font.raleway)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF5E5F60),
                        )
                    )
                }
            }
        }
    }
}

