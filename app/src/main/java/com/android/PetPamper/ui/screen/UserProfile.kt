package com.android.PetPamper.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.PetPamper.R

data class UserProfile(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val pawPoints: Int,
    val profilePictureUrl: String
)

@Composable
fun UserProfileScreen(userProfile: UserProfile) {
    Column(modifier = Modifier.padding(12.dp)) {  // Adding padding around the entire screen content
        // user name
        Text(
            text = "Stanley Cohen",
            style = TextStyle(
                fontSize = 38.sp,
                lineHeight = 45.6.sp,
                fontWeight = FontWeight.W800,
                color = Color(0xFF2490DF),
                //textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)  // Space after the title
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
                Image(
                    painter = painterResource(id = R.drawable.user_image),
                    contentDescription = "image description",
                    modifier = Modifier.size(width = 174.dp, height = 167.dp),
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier
                        .padding(12.dp), // Adds padding around the entire column content
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Adds vertical space between the children
                ){
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
                text = "${userProfile.phoneNumber}\n${userProfile.email}\n${userProfile.address}",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.W400,
                    color = Color(0xFF11347A)
                ),
                modifier = Modifier.fillMaxWidth().padding(top=15.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))  // Space between the sections
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.Top,
        ) {
            Box(Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                .width(216.dp)
                .height(100.dp)
                .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
            ){
                Text(
                    text = "My PawPoints",
                    modifier = Modifier
                        .width(91.dp)
                        .height(24.dp),

                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        //fontFamily = FontFamily(Font(R.font.roboto slab)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF11347A),
                        )
                )
                Spacer(modifier = Modifier.height(16.dp).padding(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.Top,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ellipse1),
                        contentDescription = "image description",
                        modifier = Modifier
                            .padding(1.dp)
                            .width(58.dp)
                            .height(58.dp),
                        contentScale = ContentScale.None
                    )
                    Column(
                        modifier = Modifier
                            .padding(12.dp), // Adds padding around the entire column content
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Adds vertical space between the children
                    ) {
                        // First Surface block
                        Surface(
                            modifier = Modifier
                                .width(93.dp)
                                .height(18.dp)
                                .background(color = Color(0x7A2490DF), shape = RoundedCornerShape(size = 8.dp))
                                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            // Content inside the first Surface
                            // Example: Padding applied within the Surface content area

                        }

                        // Second Surface block
                        Surface(
                            modifier = Modifier
                                .width(93.dp)
                                .height(18.dp)
                                .background(color = Color(0x7A2490DF), shape = RoundedCornerShape(size = 8.dp))
                                .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            // Content inside the second Surface
                            // Example: Padding applied within the Surface content area

                        }
                }
                }
            }
            Column(
                modifier = Modifier
                    .padding(12.dp), // Adds padding around the entire column content
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp) // Adds vertical space between the children
            ) {
                // First Surface block
                Box(
                    modifier = Modifier
                        .width(172.dp)
                        .height(43.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(8.dp))  // Ensures that the background is clipped to the rounded corners
                        .background(Color(0xFFF4F3F3))
                        .padding(8.dp),
                    //shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = "My pets",
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                fontSize = 14.sp,
                                //fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF11347A),
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.paw),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )

                    }
                }

                // Second Surface block
                Box(
                    modifier = Modifier
                        .width(172.dp)
                        .height(43.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(8.dp))  // Ensures that the background is clipped to the rounded corners
                        .background(Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
                        .padding(8.dp),
                    //shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = "My history",
                            modifier = Modifier.weight(1f),
                            style = TextStyle(
                                fontSize = 14.sp,
                                //fontFamily = FontFamily(Font(R.font.raleway)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF11347A),
                            )
                        )
                        Image(
                            painter = painterResource(id = R.drawable.history),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }


        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(Modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
            .width(395.dp)
            .height(43.dp)
            .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp))
            .padding(12.dp),

        ){
            Text(
                text = "Edit profile",
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
                modifier = Modifier
                    .size(width = 126.dp, height = 129.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                    .padding(4.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFFF4F3F3) // Sets the background color of the Surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp), // Adds padding around the entire column content
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Adds vertical space between the children
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
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            //fontFamily = FontFamily(Font(R.font.raleway)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF758287),
                        )
                    )
                }
            }
            // Second square with shadow and rounded corners
            Surface(
                modifier = Modifier
                    .size(width = 126.dp, height = 129.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
                    .padding(4.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFFF4F3F3) // Sets the background color of the Surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp), // Adds padding around the entire column content
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Adds vertical space between the children
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favorites),
                        modifier = Modifier
                            .width(88.dp)
                            .height(82.dp),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = "My Favorites",
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            //fontFamily = FontFamily(Font(R.font.raleway)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF788589),
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(124.dp))
        Box(Modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(32.dp))
            .width(395.dp)
            .height(75.dp)
            .background(color = Color(0xFFF4F3F3), shape = RoundedCornerShape(size = 8.dp)),
            ){
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
                    contentScale = ContentScale.FillBounds
                )
                Text(
                    text = "Settings",
                    modifier = Modifier
                        .width(357.dp)
                        .height(65.dp),
                    style = TextStyle(
                        fontSize = 24.sp,
                        //fontFamily = FontFamily(Font(R.font.raleway)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5E5F60),
                    )
                )
            }
        }
    }
}

@Composable
fun UserProfileDetail(label: String, detail: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Text(
            text = detail,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}

// Define other composable functions for profile picture, paw points, etc.

@Preview(showBackground = true)
@Composable
fun PreviewUserProfile() {
    val sampleUserProfile = UserProfile(
        name = "Stanley Cohen",
        phoneNumber = "+33 6 77 66 55 44",
        email = "stanley.coh@gmail.com",
        address = "Rue Louis Favre 4, 1024 Ecublens",
        pawPoints = 75,
        profilePictureUrl = "https://yourimageurl.com/image.jpg"
    )
    MaterialTheme {
        UserProfileScreen(userProfile = sampleUserProfile)
    }
}
