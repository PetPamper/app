package com.android.PetPamper.ui.screen.users

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
import coil.compose.rememberImagePainter
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.model.UserViewModel
import com.android.PetPamper.model.User


@Composable
fun UserProfileScreen(navController: NavController, userVM: UserViewModel) {
  var user by remember { mutableStateOf(userVM.getUser()) }

    var showEditProfile by remember { mutableStateOf(false) }

    var needRecompose by remember { mutableStateOf(false) }

    var showCamera by remember { mutableStateOf(false) }

    if (showCamera){
        CaptureCamera(user.email)
    }else{
        if (showEditProfile){
            EditProfileDialog(
                onDismiss = {
                    showEditProfile = false
                    needRecompose = true},
                onSave = {
                    userVM.updateUser(name = it.name, email = it.email, phone = it.phoneNumber)
                    showEditProfile = false
                    needRecompose = true},
                user)
        }

        LaunchedEffect(userVM.uid, needRecompose) {
            Log.d("LaunchedEffectTAG","UserProfileScreen recomposed")
            user = userVM.getUser()
            needRecompose = false
        }

        Log.d("UserProfile1", "phone: ${user.phoneNumber}")

        Column(modifier = Modifier.padding(12.dp)) { // Adding padding around the entire screen content
            // user name
            Text(
                text = user.name,
                style =
                TextStyle(
                    fontSize = 38.sp,
                    lineHeight = 45.6.sp,
                    fontWeight = FontWeight.W800,
                    color = Color(0xFF2490DF),
                    // textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp) // Space after the title
            )
            Box(
                modifier =
                Modifier.fillMaxWidth()
                    .height(194.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column() {


                        if(user.profilePictureUrl.isEmpty()){
                            Image(
                                painter = painterResource(id = R.drawable.user_image),
                                contentDescription = "User profile image",
                                modifier = Modifier.size(width = 160.dp, height = 150.dp),
                                contentScale = ContentScale.FillBounds)
                        }else{
                            Image(
                                painter = rememberImagePainter(user.profilePictureUrl),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(width = 160.dp, height = 150.dp),
                                contentScale = ContentScale.FillBounds)
                        }
                        Button(
                            onClick = { showCamera = true },
                            modifier =
                            Modifier.width(90.dp) // Adjusted for visual balance
                                .height(40.dp)
                                .padding(start = 75.dp),
                            // Adjusted for visual balance
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            contentPadding =
                            PaddingValues(0.dp) // Ensure there's no padding inside the button
                        ) {
                            Box(
                                contentAlignment =
                                Alignment.Center, // This centers the content inside the Box
                                modifier =
                                Modifier.fillMaxSize()
                                    .padding(0.dp) // This makes the Box fill the Button
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(), // Ensure the Row fills the Box
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start) {
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
                                        ))
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
                            modifier = Modifier.width(21.dp).height(23.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds)
                        Image(
                            painter = painterResource(id = R.drawable.email),
                            modifier = Modifier.width(16.dp).height(15.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds)
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            modifier = Modifier.width(16.dp).height(19.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds)
                    }
                    Text(
                        text =
                        "${user.phoneNumber}\n${user.email.take(20)}\n${user.address.street.take(20)}",
                        style =
                        TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.W400,
                            color = Color(0xFF11347A)),
                        modifier = Modifier.fillMaxWidth().padding(top = 15.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Space between the sections
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier =
                    Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                        .width(216.dp)
                        .height(100.dp)
                        .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                        .padding(12.dp) // Simplified padding application
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Space between Text and Row
                    ) {
                        Text(
                            text = "My PawPoints",
                            style =
                            TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF11347A)))

                        Row(
                            horizontalArrangement =
                            Arrangement.spacedBy(12.dp), // Space between image and column
                            verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ellipse1),
                                contentDescription = "image description",
                                modifier =
                                Modifier.size(58.dp).padding(1.dp) // Padding around the image
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement =
                                Arrangement.spacedBy(8.dp) // Space between two surface blocks
                            ) {
                                Surface(
                                    modifier = Modifier.width(93.dp).height(18.dp),
                                    color = Color(0x7A2490DF),
                                    shape = RoundedCornerShape(8.dp),
                                ) {
                                    Text(
                                        modifier = Modifier.width(50.dp).height(11.dp),
                                        text = "My rewards",
                                        style =
                                        TextStyle(
                                            fontSize = 9.sp,
                                            lineHeight = 10.8.sp,
                                            // fontFamily = FontFamily(Font(R.font.raleway)),
                                            fontWeight = FontWeight(700),
                                            color = Color(0xFF11347A),
                                            textAlign = TextAlign.Center))
                                }

                                Surface(
                                    modifier = Modifier.width(93.dp).height(18.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x7A2490DF)) {
                                    Text(
                                        modifier = Modifier.width(61.dp).height(7.dp),
                                        text = "My reviews",
                                        style =
                                        TextStyle(
                                            fontSize = 9.sp,
                                            lineHeight = 10.8.sp,
                                            // fontFamily = FontFamily(Font(R.font.raleway)),
                                            fontWeight = FontWeight(700),
                                            color = Color(0xFF11347A),
                                            textAlign = TextAlign.Center,
                                        ))
                                }
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
                    // First Surface block
                    Box(
                        modifier =
                        Modifier.width(172.dp)
                            .height(43.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                            .clip(
                                RoundedCornerShape(
                                    8.dp)) // Ensures that the background is clipped to the rounded
                            // corners
                            .background(Color(0xFFF4F3F3))
                            .padding(8.dp),
                        // shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                            verticalAlignment = Alignment.Top,
                        ) {
                            ClickableText(
                                onClick = { navController.navigate("PetListScreen") },
                                text = AnnotatedString("My pets"),
                                modifier = Modifier.weight(1f),
                                style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    // fontFamily = FontFamily(Font(R.font.raleway)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF11347A),
                                    textAlign = TextAlign.Center))

                            Image(
                                painter = painterResource(id = R.drawable.paw),
                                contentDescription = "image description",
                                contentScale = ContentScale.FillBounds)
                        }
                    }

                    // Second Surface block
                    Box(
                        modifier =
                        Modifier.width(172.dp)
                            .height(43.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                            .clip(
                                RoundedCornerShape(
                                    8.dp)) // Ensures that the background is clipped to the rounded
                            // corners
                            .background(Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                            .padding(8.dp),
                        // shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = "My history",
                                modifier = Modifier.weight(1f),
                                style =
                                TextStyle(
                                    fontSize = 14.sp,
                                    // fontFamily = FontFamily(Font(R.font.raleway)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF11347A),
                                    textAlign = TextAlign.Center))

                            Image(
                                painter = painterResource(id = R.drawable.history),
                                contentDescription = "image description",
                                contentScale = ContentScale.FillBounds)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                contentAlignment = Alignment.Center, // Centers the Text over the Button
                modifier =
                Modifier.width(395.dp)
                    .height(43.dp)
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))) {
                Button(
                    onClick = { showEditProfile = true},
                    modifier =
                    Modifier.matchParentSize(), // Ensures the Button matches the size of the Box
                    colors =
                    ButtonDefaults.buttonColors(Color.Transparent), // Makes the Button transparent
                    elevation =
                    ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        focusedElevation = 0.dp) // Removes elevation to avoid shadow
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
                        textAlign = TextAlign.Center),
                    modifier =
                    Modifier.align(
                        Alignment.Center) // Ensures the Text is centered in the Box, over the Button
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Two smaller squares underneath
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp), // Adds padding around the row
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                // First square with shadow and rounded corners
                Surface(
                    modifier =
                    Modifier.size(width = 126.dp, height = 129.dp)
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
                            modifier = Modifier.width(88.dp).height(82.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds)
                        Text(
                            text = "My Wallet",
                            style =
                            TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                // fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF758287),
                            ))
                    }
                }
                // Second square with shadow and rounded corners
                Surface(
                    modifier =
                    Modifier.size(width = 126.dp, height = 129.dp)
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
                            painter = painterResource(id = R.drawable.favorites),
                            modifier = Modifier.width(88.dp).height(82.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds)
                        Text(
                            text = "My Favorites",
                            style =
                            TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                // fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF788589),
                            ))
                    }
                }
            }
            Spacer(modifier = Modifier.height(124.dp))
            Box(
                Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                    .width(395.dp)
                    .height(75.dp)
                    .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Image(
                        modifier = Modifier.width(30.dp).height(65.dp),
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds)
                    Text(
                        text = "Settings",
                        modifier = Modifier.width(357.dp).height(65.dp),
                        style =
                        TextStyle(
                            fontSize = 24.sp,
                            // fontFamily = FontFamily(Font(R.font.raleway)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF5E5F60),
                        ))
                }
            }
        }
    }


}

@Composable
fun UserProfileDetail(label: String, detail: String) {
  Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(text = label, fontSize = 16.sp, color = Color.Gray)
    Text(text = detail, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
  }
}

// Define other composable functions for profile picture, paw points, etc.

@Preview(showBackground = true)
@Composable
fun PreviewUserProfile() {
    class previewUVM: UserViewModel("alitennis131800@gmail.com"){
        override fun getUser(force: Boolean): User {
            return User("Ali Tennis",super.uid,"0041234567890", Address("Rte des Essai 40, Apt. 4","Essex","Suisse","1234",
                LocationMap()
            )
            )
        }
    }
    MaterialTheme { UserProfileScreen(userVM = previewUVM(), navController = NavController(context = LocalContext.current)) }
}
