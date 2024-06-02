package com.android.PetPamper.ui.screen.groomers

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.android.PetPamper.R
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.model.Reservation
import com.android.PetPamper.ui.screen.users.PetListViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun groomerReservations(
    navController: NavController,
    email: String?,
) {
    val firebaseConnection = FirebaseConnection()
    val reservation = remember { mutableStateOf(listOf<Reservation>()) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(email) {
        firebaseConnection.fetchGroomerReservations(email!!) {
            reservation.value = it
            isLoading.value = false
        }
    }

    if (isLoading.value) {
        // Display a loading indicator while fetching reservations
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }



    }


    if (reservation.value.isEmpty()) {
        Text(
            text = "No reservations found",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    }

    else {
        val pageState2 = rememberPagerState(initialPage = 0, pageCount = { reservation.value.size })
        val scope2 = rememberCoroutineScope()
        val currentReservations = reservation.value[pageState2.currentPage]

        Scaffold {
            it
            Column {
                Row(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "Your Reservations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        enabled = pageState2.currentPage > 0,
                        onClick = {
                            scope2.launch { pageState2.animateScrollToPage(pageState2.currentPage - 1) }
                        }) {
                        Icon(Icons.Default.KeyboardArrowLeft, null)
                    }

                    HorizontalPager(
                        state = pageState2,
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        modifier = Modifier
                            .height(180.dp)
                            .weight(1f)
                    ) { page ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            modifier =
                            Modifier.graphicsLayer {
                                val pageOffset =
                                    pageState2.getOffsetFractionForPage(page).absoluteValue
                                lerp(
                                    start = 0.50f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                    .also { scale ->
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                alpha =
                                    lerp(
                                        start = 0.50f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                            }) {
                            Box(
                                modifier =
                                Modifier
                                    .padding(16.dp)
                                    // .background(Color.White, RoundedCornerShape(10.dp))
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
                                    Spacer(
                                        modifier = Modifier
                                            .height(8.dp)
                                            .width(10.dp)
                                    )

                                    // Details: Name, age, distance
                                    Column {
                                        Text(
                                            text = currentReservations.groomerName,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = "Price: ${currentReservations.price}")
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(
                                            text =
                                            "Experience Years: ${currentReservations.experienceYears} years"
                                        )
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(text = "Date : ${currentReservations.date}")
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Text(text = "Hour: ${currentReservations.hour}")
                                    }
                                }

                                // Spacer
                                // Spacer(modifier = Modifier.height(5.dp))

                                // Additional details: Service type, date joined, total price

                                // Start chatting button

                            }
                        }
                    }

                    IconButton(
                        enabled = pageState2.currentPage < pageState2.pageCount - 1,
                        onClick = {
                            scope2.launch { pageState2.animateScrollToPage(pageState2.currentPage + 1) }
                        }) {
                        Icon(Icons.Default.KeyboardArrowRight, null)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {  },
                    colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2490DF), contentColor = Color.White
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Show your Reservations", fontSize = 12.sp)
                }
            }
        }
    }
}

l