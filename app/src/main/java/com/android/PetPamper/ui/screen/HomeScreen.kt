package com.android.PetPamper.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.size.Scale
import com.android.PetPamper.R
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


@Composable
fun HomeScreen(navController: NavController, email: String?){
    Column {
        //Text(text = "Home screen")
        CarouselCard(navController, email)
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselCard(navController: NavController, email: String?){
    val sliderList = listOf(
        "https://picsum.photos/id/237/800/500",
        "https://picsum.photos/id/244/800/500",
        "https://picsum.photos/id/239/800/500",
        "https://picsum.photos/id/243/800/500",
        "https://picsum.photos/id/236/800/500"
    )
    val pageState = rememberPagerState(initialPage = 2, pageCount = { sliderList.size })
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(20.dp)) {
            Text(text="Your pets", fontWeight = FontWeight.Bold, fontSize =20.sp)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = pageState.currentPage > 0,
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(pageState.currentPage-1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowLeft,null)
            }

            HorizontalPager(state = pageState,
                contentPadding = PaddingValues(horizontal = 5.dp ),
                modifier = Modifier
                    .height(300.dp)
                    .width(350.dp)
                    .weight(1f)
            ) {
                page -> Card(
                    shape= RoundedCornerShape(10.dp),
                    modifier = Modifier.graphicsLayer {
                        val pageOffset = pageState.getOffsetFractionForPage(page).absoluteValue
                        lerp(
                            start= 0.50f,
                            stop = 1f,
                            fraction = 1f- pageOffset.coerceIn(0f,1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start= 0.50f,
                            stop = 1f,
                            fraction = 1f- pageOffset.coerceIn(0f,1f)
                        )
                    }
                ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                Text(text = "Floyd", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text(text = "11 years old", color = Color.DarkGray)
                Text(text = "Description", fontWeight = FontWeight.Bold, color = Color.Blue)
                Text(text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s", color = Color.DarkGray)


                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f), // or your desired aspect ratio
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(sliderList[page])
                            .crossfade(true)
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.placeholder),
                        error = painterResource(id = R.drawable.error_image_generic),
                        modifier = Modifier.fillMaxSize() // Make the image fill the Box
                    )
                }

                Spacer(modifier = Modifier
                    .height(8.dp)
                    .width(10.dp))
            }
            }
                IconButton(
                    enabled = pageState.currentPage < pageState.pageCount - 1,
                    onClick = {
                        scope.launch {
                            pageState.animateScrollToPage(pageState.currentPage+1)
                        }
                    }
                ) {
                    Icon(Icons.Default.KeyboardArrowRight,null)
                }
        }
        /*Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(sliderList.size){it ->
                val color =
                    if (pageState.currentPage == it) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(20.dp)
                        .background(color)
                        .clickable {
                            scope.launch {
                                pageState.animateScrollToPage(it)
                            }
                        }
                ){

                }
            }
        }*/


//            ClickableText(
//                text = AnnotatedString("Show your pets"),
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally),
//                style = TextStyle(
//                    textAlign = TextAlign.Center),
//                onClick = {
//                    navController.navigate("PetListScreen/$email")
//                }
//            )

        Button(
            onClick = { navController.navigate("PetListScreen") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Show your pets", fontSize = 9.sp)
        }



        // second one



        val pageState2 = rememberPagerState(initialPage = 2, pageCount = { sliderList.size })
        val scope2 = rememberCoroutineScope()

        Row(modifier = Modifier.padding(15.dp)) {
            Text(text="Your Reservations", fontWeight = FontWeight.Bold, fontSize =20.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                enabled = pageState2.currentPage > 0,
                onClick = {
                    scope2.launch {
                        pageState2.animateScrollToPage(pageState2.currentPage-1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowLeft,null)
            }

            HorizontalPager(state = pageState2,
                contentPadding = PaddingValues(horizontal = 10.dp ),
                modifier = Modifier
                    .height(180.dp)
                    .weight(1f)
            ) {
                    page -> Card(
                shape= RoundedCornerShape(10.dp),
                modifier = Modifier.graphicsLayer {
                    val pageOffset = pageState2.getOffsetFractionForPage(page).absoluteValue
                    lerp(
                        start= 0.50f,
                        stop = 1f,
                        fraction = 1f- pageOffset.coerceIn(0f,1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    alpha = lerp(
                        start= 0.50f,
                        stop = 1f,
                        fraction = 1f- pageOffset.coerceIn(0f,1f)
                    )
                }
            ) {

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        //.background(Color.White, RoundedCornerShape(10.dp))
                        .height(150.dp)
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        // Profile picture
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                        )

                        // Spacer
                        Spacer(modifier = Modifier
                            .height(8.dp)
                            .width(10.dp))

                        // Details: Name, age, distance
                        Column {
                            Text(text = "John Doe", fontWeight = FontWeight.Bold)
                            Text(text = "Age: 30")
                            Text(text = "Distance: 5 km")
                        }
                    }

                    // Spacer
                    //Spacer(modifier = Modifier.height(5.dp))

                    // Additional details: Service type, date joined, total price
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text(text = "Service Type: Service XYZ")
                        Text(text = "Date: Jan 1, 2024")
                        Text(text = "Total: $100")
                        Spacer(modifier = Modifier.height(10.dp))
                    }



                    // Start chatting button
                    Button(
                        onClick = { /* Handle start chatting action */ },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text("Chat")
                    }
                }




            }
            }
            IconButton(
                enabled = pageState2.currentPage < pageState2.pageCount - 1,
                onClick = {
                    scope2.launch {
                        pageState2.animateScrollToPage(pageState2.currentPage+1)
                    }
                }
            ) {
                Icon(Icons.Default.KeyboardArrowRight,null)
            }
        }
        /*Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(sliderList.size){it ->
                val color =
                    if (pageState2.currentPage == it) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .size(20.dp)
                        .background(color)
                        .clickable {
                            scope2.launch {
                                pageState2.animateScrollToPage(it)
                            }
                        }
                ){

                }
            }
        }*/

//        ClickableText(
//            text = AnnotatedString("Show your Reservations"),
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            style = TextStyle(textAlign = TextAlign.Center),
//            onClick = {
//                navController.navigate("ReservationsScreen/$email")
//            }
//        )

        Button(
            onClick = { navController.navigate("ReservationsScreen") },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Show your Reservations", fontSize = 9.sp)
        }

    }
}

