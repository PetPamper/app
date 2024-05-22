package com.android.PetPamper.ui.screen.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import com.android.PetPamper.ui.dialogs.AddressUpdateDialog

data class GroomerReview(
    val email: String,
    val name: String,
    val types: String,
    val price: String,
    val distance: String,
    val reviewCount: Int,
    val rating: Number,
    val profilePictureUrl: String,
)

@Composable
fun GroomerItem(groomer: GroomerReview, navController: NavController) {
  Card(
      backgroundColor = Color(0xFFF7F7F7),
      elevation = 4.dp,
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .clickable(
                  onClick = { navController.navigate("groomer_details/${groomer.email}") })) {
        //   Navigate to groomer profile) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
              verticalAlignment = Alignment.Top, // Ensure vertical alignment to top
              modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter =
                        rememberImagePainter(
                            data = groomer.profilePictureUrl,
                            builder = {
                              crossfade(true)
                              placeholder(R.drawable.bar_groomers)
                            }),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(48.dp).clip(CircleShape), // Clip image to circle
                    contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(16.dp)) // Spacing between image and text
                Column(
                    modifier =
                        Modifier.weight(
                                1f) // Use weight to allow the column to fill available space
                            .align(
                                Alignment
                                    .CenterVertically) // Vertically center the text relative to the
                    // image
                    ) {
                      Text(text = groomer.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                      Text(
                          fontSize = 14.sp,
                          text =
                              buildAnnotatedString {
                                withStyle(style = SpanStyle(color = Color(0xFF2490DF))) {
                                  append("Groomable Pet Types: ")
                                }
                                withStyle(style = SpanStyle(color = Color.Black)) {
                                  append(groomer.types)
                                }
                              })
                    }
                RatingBox(
                    rating = groomer.rating.toDouble(),
                    starPainter = painterResource(id = R.drawable.star))
              }
          Spacer(modifier = Modifier.height(8.dp))
          Divider()
          Spacer(modifier = Modifier.height(8.dp))
          Row(
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()) {
                Text(
                    text =
                        buildAnnotatedString {
                          withStyle(style = SpanStyle(color = Color(0xFF2490DF))) {
                            append("Price\n")
                          }
                          withStyle(style = SpanStyle(color = Color.Black)) {
                            append("${groomer.price}/hour")
                          }
                        })
                Text(
                    text =
                        buildAnnotatedString {
                          withStyle(style = SpanStyle(color = Color(0xFF2490DF))) {
                            append("Distance\n")
                          }
                          withStyle(style = SpanStyle(color = Color.Black)) {
                            append("${groomer.distance}")
                          }
                        })
                Text(
                    text =
                        buildAnnotatedString {
                          withStyle(style = SpanStyle(color = Color(0xFF2490DF))) {
                            append("Reviews\n")
                          }
                          withStyle(style = SpanStyle(color = Color.Black)) {
                            append("${groomer.reviewCount}")
                          }
                        })
              }
        }
      }
}

@Composable
fun GroomerList(groomers: List<GroomerReview>, navController: NavController) {
  LazyColumn(Modifier.background(Color.White)) {
    items(groomers) { groomer -> GroomerItem(groomer = groomer, navController) }
  }
}

@Composable
fun RatingBox(rating: Double, starPainter: Painter) {
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.clip(RoundedCornerShape(6.dp))
              .background(Color.White)
              .padding(horizontal = 2.dp, vertical = 2.dp) // Adjust padding as needed
              .size(width = 50.dp, height = 24.dp)

      // Adjust size as needed
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
              text = "$rating",
              color = Color(0xFFFDD835),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold)
          Icon(
              painter = starPainter,
              contentDescription = "Star",
              tint = Color(0xFFFDD835),
              modifier =
                  Modifier.size(16.dp) // Adjust the size of the star as needed
                      .padding(start = 2.dp) // Space between text and star
              )
        }
      }
}

@Composable
fun GroomerTopBar(
    address: Address,
    onUpdateAddress: (Address) -> Unit,
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
  var showDialog by remember { mutableStateOf(false) }

  if (showDialog) {
    AddressUpdateDialog(
        initialAddress = address,
        onDismiss = { showDialog = false },
        onSave = {
          onUpdateAddress(it)
          showDialog = false
        })
  }

  TopAppBar(modifier = Modifier.height(80.dp), backgroundColor = Color.White, elevation = 4.dp) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          // Location and dropdown arrow
          Column {
            Text(text = "Current Location : ", style = MaterialTheme.typography.subtitle1)

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text =
                      if (address.street.length < 30) {
                        address.street
                      } else {
                        address.street.take(30) + "..."
                      },
                  color = Color.Black,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold)

              IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.size(24.dp))
              }
            }
          }

          // Search and notification icons
          Row {
            IconButton(onClick = onSearchClick) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Search",
                  modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = onNotificationsClick) {
              Icon(
                  imageVector = Icons.Default.Notifications,
                  contentDescription = "Notifications",
                  modifier = Modifier.size(24.dp))
            }
          }
        }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroomerList() {
  val sampleGroomers =
      listOf(
          GroomerReview(
              "@.",
              "Will Parker",
              "Dog, Cat",
              "50$",
              "1,5 KM",
              26,
              4.4,
              "https://img.freepik.com/psd-gratuit/personne-celebrant-son-orientation-sexuelle_23-2150115662.jpg"),
          GroomerReview(
              "@.",
              "Kobe Bryant",
              "Dog, Cat, Hamster",
              "65$",
              "2 KM",
              13,
              4.5,
              "https://www.livreshebdo.fr/sites/default/files/styles/article_principal/public/assets/images/106092057_1566487914671gettyimages_1095029036.jpeg?itok=KQgvBUB3"),
          GroomerReview(
              "@.",
              "Cristiano Ronaldo",
              "Dog",
              "35$",
              "8 KM",
              53,
              4.8,
              "https://cdn-s-www.ledauphine.com/images/0A36430E-64F8-4FC1-A61F-6BEDB90FDC94/NW_raw/le-depart-de-cristiano-ronaldo-vers-la-juventus-turin-a-ete-officialise-par-le-real-madrid-mardi-soir-quelques-heures-avant-la-demi-finale-de-coupe-du-monde-france-belgique-photo-ander-gillenea-afp-1531297805.jpg"))
  //  MaterialTheme { GroomerList(groomers = sampleGroomers, navController = ) }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroomerTopBar() {
  MaterialTheme {
    GroomerTopBar(
        address = Address("1234 Main St", "City", "State", "12345", LocationMap()),
        onUpdateAddress = { /* Preview does not update address */})
  }
}
