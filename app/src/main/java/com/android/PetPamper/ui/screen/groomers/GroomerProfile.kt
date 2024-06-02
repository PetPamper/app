package com.android.PetPamper.ui.screen.groomers

import com.android.PetPamper.ui.screen.users.EditProfileDialog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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

    Column(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        // User name
        Text(
            text = user.name,
            style = TextStyle(
                fontSize = 38.sp,
                lineHeight = 45.6.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF2490DF),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Box(
            modifier = Modifier
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
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.user_image),
                        contentDescription = "User profile image",
                        modifier = Modifier.size(width = 160.dp, height = 150.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Button(
                        onClick = { /* Handle your click here */ },
                        modifier = Modifier
                            .width(90.dp)
                            .height(40.dp)
                            .padding(start = 75.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize().padding(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.editicon),
                                    contentDescription = "Edit Photo",
                                    modifier = Modifier.size(15.dp),
                                    alignment = Alignment.CenterStart,
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "Edit photo",
                                    style = TextStyle(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    text = "${user.phoneNumber}\n${user.email.take(20)}\n${user.address.street.take(20)}",
                    style = TextStyle(
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
        Spacer(modifier = Modifier.height(8.dp))
        // Box 1: Verify Identity
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row{
                Text(
                    text = "Verify Identity",
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF11347A),
                        textAlign = TextAlign.Center,
                    )
                )
                Image(
                    painter = painterResource(id = R.drawable.verify_id),
                    contentDescription = "Verify Identity Icon",
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row{
                Text(
                    text = "Edit Profile",
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF11347A),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Box 2: My Services
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(                        color = Color(0xFFF4F3F3),
                     shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                    Row {
                        Text(
                            text = "My Services",
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF11347A),
                                textAlign = TextAlign.Center
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.editicon),
                            contentDescription = "image description",
                            modifier = Modifier.size(13.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Surface(
                        modifier = Modifier
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                            .size(89.dp).background(color = Color(0x30737373))

                        , shape = RoundedCornerShape(32.dp)
                    ) {
                        Text(
                            text = "Bathing and Trimming",
                            modifier = Modifier.padding(8.dp),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight(700),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                            .size(89.dp)
                            .background(color = Color(0x30737373), shape = RoundedCornerShape(32.dp))
                    ) {
                        Text(
                            text = "Nail Trimming",
                            modifier = Modifier.padding(8.dp),
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight(700),
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Box 3: My Pets
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                .background(                        color = Color(0xFFF4F3F3),
                     shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ){
                Row {
                    Text(
                        text = "My Pets",
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF11347A),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img),
                        contentDescription = "image description",
                        modifier = Modifier.size(20.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the settings button to the bottom
        // Settings button
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                .fillMaxWidth()
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "image description",
                    modifier = Modifier.size(30.dp),
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = "Settings",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5E5F60),
                    ),
                    modifier = Modifier.weight(1f)
                )
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
