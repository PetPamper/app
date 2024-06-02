package com.android.PetPamper.ui.screen.users

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.android.PetPamper.database.PetDataHandler
import com.android.PetPamper.model.Reservation
import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, email: String?) {
  Column {
    val firebaseConnection = FirebaseConnection.getInstance()
    var resa = remember { mutableStateOf(listOf<Reservation>()) }
    firebaseConnection.fetchReservations(email!!) { resa.value = it }

    CarouselCard(
        navController,
        email,
        PetListViewModel(email, PetDataHandler(firebaseConnection)),
        resa.value)
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselCard(
    navController: NavController,
    email: String?,
    petListViewModel: PetListViewModel,
    reservation: List<Reservation>
) {

  val sliderList by remember { mutableStateOf(petListViewModel.petsList) }
  val pageState = rememberPagerState(initialPage = 0, pageCount = { sliderList.size })
  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()

  Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
    Row(modifier = Modifier.padding(15.dp)) {
      Text(text = "Your pets", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
    }


    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      if (sliderList.isNotEmpty()) {
        IconButton(
            enabled = pageState.currentPage > 0,
            onClick = {
              scope.launch { pageState.animateScrollToPage(pageState.currentPage - 1) }
            }) {
              Icon(Icons.Default.KeyboardArrowLeft, null)
            }

        HorizontalPager(
            state = pageState,
            contentPadding = PaddingValues(horizontal = 5.dp),
            modifier = Modifier.height(300.dp).width(350.dp).weight(1f)) { page ->
              val pet = petListViewModel.petsList.getOrNull(page)
              Card(
                  shape = RoundedCornerShape(10.dp),
                  modifier =
                      Modifier.graphicsLayer {
                        val pageOffset = pageState.getOffsetFractionForPage(page).absoluteValue
                        lerp(start = 0.50f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                            .also { scale ->
                              scaleX = scale
                              scaleY = scale
                            }
                        alpha =
                            lerp(
                                start = 0.50f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f))
                      }) {
                    if (pet != null) {
                      Column(modifier = Modifier.padding(16.dp)) {
                        // Pet name
                        Text(
                            text = pet.name,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2490DF))
                        // Pet age
                        Text(
                            text = "${pet.birthDate.until(LocalDate.now()).years} years old",
                            color = Color.DarkGray)
                        // Pet description
                        Text(
                            text = "Description",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2490DF))
                        Text(
                            text =
                                if (petListViewModel.petsList[page].description.length > 150) {
                                  petListViewModel.petsList[page].description.take(150) + "..."
                                } else petListViewModel.petsList[page].description,
                            color = Color.DarkGray)
                      }
                      Box(
                          modifier =
                              Modifier.fillMaxWidth()
                                  .aspectRatio(16f / 9f), // or your desired aspect ratio
                          contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model =
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(sliderList[page].pictures.getOrNull(0))
                                        .crossfade(true)
                                        .scale(Scale.FILL)
                                        .build(),
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.placeholder),
                                error = painterResource(id = R.drawable.error_image_generic),
                                modifier = Modifier.fillMaxSize() // Make the image fill the Box
                                )
                          }

                      Spacer(modifier = Modifier.height(8.dp).width(10.dp))
                    }
                  }
            }
        IconButton(
            enabled = pageState.currentPage < pageState.pageCount - 1,
            onClick = {
              scope.launch { pageState.animateScrollToPage(pageState.currentPage + 1) }
            }) {
              Icon(Icons.Default.KeyboardArrowRight, null)
            }
      } else {
        Box(modifier = Modifier.fillMaxWidth()) {
          Text(
              text = "Empty",
              color = Color.Gray,
              modifier = Modifier.align(Alignment.Center).padding(16.dp))
        }
      }

    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
        onClick = { navController.navigate("PetListScreen") },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2490DF), contentColor = Color.White)) {
          Text("Show your pets", fontSize = 12.sp)
        }

    // second one

    val pageState2 = rememberPagerState(initialPage = 0, pageCount = { reservation.size })
    val scope2 = rememberCoroutineScope()
    val currentReservations = reservation.getOrNull(pageState2.currentPage)

    Row(modifier = Modifier.padding(15.dp)) {
      Text(
          text = "Your Reservations",
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          color = Color.Gray)
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

      if (reservation.isNotEmpty()) {
        val currentReservations = reservation[pageState2.currentPage]
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
            modifier = Modifier.height(180.dp).weight(1f)) { page ->
              Card(
                  shape = RoundedCornerShape(10.dp),
                  modifier =
                      Modifier.graphicsLayer {
                        val pageOffset = pageState2.getOffsetFractionForPage(page).absoluteValue
                        lerp(start = 0.50f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
                            .also { scale ->
                              scaleX = scale
                              scaleY = scale
                            }
                        alpha =
                            lerp(
                                start = 0.50f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f))
                      }) {
                    Box(
                        modifier =
                            Modifier.padding(16.dp)
                                // .background(Color.White, RoundedCornerShape(10.dp))
                                .height(150.dp)
                                .fillMaxWidth()) {
                          if (currentReservations != null) {
                            Row(modifier = Modifier.padding(8.dp)) {

                              // Profile picture
                              Image(
                                  painter = painterResource(id = R.drawable.profile),
                                  contentDescription = "Profile Picture",
                                  modifier = Modifier.size(60.dp).clip(CircleShape))

                              // Spacer
                              Spacer(modifier = Modifier.height(8.dp).width(10.dp))

                              // Details: Name, age, distance
                              Column {
                                Text(
                                    text = currentReservations.groomerName,
                                    fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(text = "Price: ${currentReservations.price}")
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text =
                                        "Experience Years: ${currentReservations.experienceYears} years")
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(text = "Date : ${currentReservations.date}")
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(text = "Hour: ${currentReservations.hour}")
                              }
                            }


                            // Spacer
                            // Spacer(modifier = Modifier.height(5.dp))

                            // Additional details: Service type, date joined, total price
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.align(Alignment.BottomCenter)) {
                                  Spacer(modifier = Modifier.height(10.dp))
                                }

                            // Start chatting button
                            Button(
                                onClick = { /* Handle start chatting action */},
                                modifier = Modifier.align(Alignment.BottomEnd),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2490DF),
                                        contentColor = Color.White)) {
                                  Text("Chat")
                                }
                          }
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
      } else {
        Box(modifier = Modifier.fillMaxWidth()) {
          Text(
              text = "Empty",
              color = Color.Gray,
              modifier = Modifier.align(Alignment.Center).padding(16.dp))
        }
      }

    }

    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = { navController.navigate("ReservationsScreen") },
        modifier = Modifier.align(Alignment.CenterHorizontally),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2490DF), contentColor = Color.White)) {
          Text("Show your Reservations", fontSize = 12.sp)
        }
  }
}
