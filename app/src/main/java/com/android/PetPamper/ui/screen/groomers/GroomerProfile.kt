package com.android.PetPamper.ui.screen.groomers

import com.android.PetPamper.ui.screen.users.EditProfileDialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.android.PetPamper.model.User
import com.android.PetPamper.model.UserViewModel

@Composable
fun GroomerProfileScreen(navController: NavController, userVM: UserViewModel) {
    var user by remember { mutableStateOf(userVM.getUser()) }

    var showEditProfile by remember { mutableStateOf(false) }

    var needRecompose by remember { mutableStateOf(false) }

    if (showEditProfile) {
        EditProfileDialog(
            onDismiss = {
                showEditProfile = false
                needRecompose = true
            },
            onSave = {
                userVM.updateUser(name = it.name, email = it.email, phone = it.phoneNumber)
                showEditProfile = false
                needRecompose = true
            },
            user)
    }

    LaunchedEffect(userVM.uid, needRecompose) {
        Log.d("LaunchedEffectTAG", "UserProfileScreen recomposed")
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
                .padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                Column() {
                    Image(
                        painter = painterResource(id = R.drawable.user_image),
                        contentDescription = "User profile image",
                        modifier = Modifier.size(width = 160.dp, height = 150.dp),
                        contentScale = ContentScale.FillBounds)
                    Button(
                        onClick = { /* Handle your click here */},
                        modifier =
                        Modifier
                            .width(90.dp) // Adjusted for visual balance
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
                            Modifier
                                .fillMaxSize()
                                .padding(0.dp) // This makes the Box fill the Button
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(), // Ensure the Row fills the Box
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start) {
                                Image(
                                    painter = painterResource(id = R.drawable.editicon),
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
                        modifier = Modifier
                            .width(21.dp)
                            .height(23.dp),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds)
                    Image(
                        painter = painterResource(id = R.drawable.email),
                        modifier = Modifier
                            .width(16.dp)
                            .height(15.dp),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds)
                    Image(
                        painter = painterResource(id = R.drawable.home),
                        modifier = Modifier
                            .width(16.dp)
                            .height(19.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Space between the sections
        Button(
            onClick = {/*implement verify identity*/},
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000)
                )
                .width(391.dp)
                .height(43.dp)
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                .padding(start = 12.dp, top = 14.dp, end = 12.dp, bottom = 69.dp)
        ){
            Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Verify Identity ",
                    modifier = Modifier
                        .width(367.dp)
                        .height(22.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        //fontFamily = FontFamily(Font(R.font.raleway)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF11347A),
                        textAlign = TextAlign.Center,)
                    )
                Image(
                    painter = painterResource(id = R.drawable.verify_id),
                    contentDescription = "image description",
                    contentScale = ContentScale.FillBounds
                )
                }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color(0x40000000),
                    ambientColor = Color(0x40000000)
                )
                .width(391.dp)
                .height(172.dp)
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 69.dp)
        ){
            Column(){
                Button(onClick = {}){
                    Row(){
                        Text(
                            text = "My Services",
                            modifier = Modifier
                                .width(367.dp)
                                .height(22.dp),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                //fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF11347A),
                                textAlign = TextAlign.Center,
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.editicon),
                            modifier = Modifier
                                .width(13.dp)
                                .height(13.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Surface(modifier= Modifier
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .width(89.dp)
                        .height(89.dp)
                        .background(
                            color = Color(0x30737373),
                            shape = RoundedCornerShape(size = 32.dp)
                        )
                    ){
                        Text(
                            text = "Bathing and Trimming",
                            modifier = Modifier
                                .width(73.dp)
                                .height(53.dp),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                //fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                    Surface(modifier= Modifier
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .width(89.dp)
                        .height(89.dp)
                        .background(
                            color = Color(0x30737373),
                            shape = RoundedCornerShape(size = 32.dp)
                        )
                    ){
                        Text(
                            text = "Nail Trimming",
                            modifier = Modifier
                                .width(87.dp)
                                .height(53.dp),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                //fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(700),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(modifier=Modifier
            .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
            .width(391.dp)
            .height(66.dp)
            .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 69.dp))
        {

               Button(onClick={},modifier=Modifier
                   .width(367.dp)
                   .height(22.dp)){
                   Row(){
                       Text(
                           text = "My Services",
                           style = TextStyle(
                               fontSize = 15.sp,
                               lineHeight = 18.sp,
                               //fontFamily = FontFamily(Font(R.font.raleway)),
                               fontWeight = FontWeight(700),
                               color = Color(0xFF11347A),
                               textAlign = TextAlign.Center,
                           )
                       )
                       Image(
                           painter = painterResource(id = R.drawable.editicon),
                           modifier =Modifier
                               .width(13.dp)
                               .height(13.dp),
                           contentDescription = "image description",
                           contentScale = ContentScale.FillBounds
                       )
                   }
               }
            Surface(modifier=Modifier
                .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
                .width(37.dp)
                .height(37.dp)
                .background(color = Color(0x30737373), shape = RoundedCornerShape(size = 32.dp))
            )
            { Image(
                painter = painterResource(id = R.drawable.img),
                modifier =Modifier
                    .width(22.dp)
                    .height(20.dp),
                contentDescription = "image description",
                contentScale = ContentScale.FillBounds
            ) //no placeholder and actually fetch the pets!!!!

            }



        }


        Spacer(modifier = Modifier.height(4.dp))
        Box(
            Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                .width(395.dp)
                .height(75.dp)
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp)),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.Top,
            ) {
                Image(
                    modifier = Modifier
                        .width(30.dp)
                        .height(65.dp),
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "image description",
                    contentScale = ContentScale.FillBounds)
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
                    ))
            }
        }
    }
}

@Composable
fun GroomerProfileDetail(label: String, detail: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = label, fontSize = 16.sp, color = Color.Gray)
        Text(text = detail, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
    }
}

// Define other composable functions for profile picture, paw points, etc.

@Preview(showBackground = true)
@Composable
fun PreviewGroomerProfile() {
    class previewUVM : UserViewModel("alitennis131800@gmail.com") {
        override fun getUser(force: Boolean): User {
            return User(
                "Ali Tennis",
                super.uid,
                "0041234567890",
                Address("Rte des Essai 40, Apt. 4", "Essex", "Suisse", "1234", LocationMap()))
        }
    }
    MaterialTheme {
        GroomerProfileScreen(
            userVM = previewUVM(), navController = NavController(context = LocalContext.current))
    }
}
