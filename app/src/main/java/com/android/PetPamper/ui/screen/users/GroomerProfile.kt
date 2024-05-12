import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberImagePainter
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.LocationMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.MarkerOptions

import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

@Composable
fun GroomerProfile(groomer: Groomer) {
    Scaffold(
        bottomBar = { BottomBookingBar(groomer.price) }  // This will place the booking bar at the bottom
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()))

        {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(groomer.profilePic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoCard(title = "Groomable Pets", description = groomer.petTypes.joinToString(", "), iconResource = R.mipmap.pets_foreground)
            Spacer(modifier = Modifier.width(10.dp))
            InfoCard( title = "Experience years", description = groomer.yearsExperience, iconResource = R.mipmap.calendar_foreground)
            Spacer(modifier = Modifier.width(10.dp))

            InfoCard( title = "Location", description = groomer.address.location.name.take(20)+"...", iconResource = R.mipmap.locationnew1_foreground)
        }
        Spacer(modifier = Modifier.height(16.dp))

        GroomerProfileCard(profilePic = groomer.profilePic, name = groomer.name.uppercase(), distance = "1.2 km")
        Spacer(modifier = Modifier.height(16.dp))
        SummarySection(summaryText = "${groomer.name.uppercase()} is a professional pet groomer with 4 years of experience. He offers a variety of services including ${groomer.services.joinToString ( " and " )}. Will is available for grooming ${groomer.petTypes.joinToString(" and ")}. His services start at ${groomer.price}$.")
        Spacer(modifier = Modifier.height(16.dp))

        ServicesSection(groomer.services)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Location", fontSize = 18.sp, color = Color.Black, modifier = Modifier.padding(8.dp))

        GroomerLocationMap(LatLng(groomer.address.location.latitude, groomer.address.location.longitude))

        Spacer(modifier = Modifier.height(80.dp))

    }
    }
}


@Composable
fun BottomBookingBar(price: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        // Define a fixed height for the bottom section
        elevation = 4.dp,
        backgroundColor = Color(0xFFEAEAEA)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Price: \n$${price}/hour",
                style = MaterialTheme.typography.h6
            )
            Button(
                onClick = {
                    // Action for the book button
                    println("Book Now Clicked!")
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2490DF)),
                modifier = Modifier
                    .height(40.dp)
                    .width(110.dp)
            ) {
                Text("Book Now", color = Color.White)
            }
        }
    }
}
@Composable
fun InfoCard(title: String, description: String, iconResource: Int) {
    Card(
        modifier = Modifier
            .padding(2.dp)
            .width(110.dp),  // Fixed width for the card
        backgroundColor = Color(0xFFF7F7F7),
        elevation = 4.dp  // Slight shadow for better UI depth
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally  // Aligns children to the center horizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconResource),
                    contentDescription = title,  // Accessibility description
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 0.dp),
                    tint = Color.Unspecified  // Uses the icon's natural color
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize =12.sp,
                    color = Color(0xFF2490DF)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))  // Space between title row and description
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 8.dp)  // Padding at the bottom of the text
                    .fillMaxWidth(),  // Ensures the text fills the width of the card
                textAlign = TextAlign.Center  // Centers the text within its container
            )
        }
    }
}


@Composable
fun GroomerLocationMap(
    location: LatLng,  // Latitude and longitude of the groomer's location
    zoom: Float = 15f  // Zoom level for map
) {
    var mapView = remember { mutableStateOf<MapView?>(null) }

        AndroidView(
            modifier = Modifier
                .width(400.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp)),

            factory = { context ->
                MapView(context).apply {
                    mapView.value = this
                    onCreate(Bundle())
                    onResume()  // Trigger the map to resume
                    getMapAsync { googleMap ->
                        googleMap.addMarker(MarkerOptions().position(location))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
                    }
                }
            },
            update = { mapView ->
                mapView.getMapAsync { googleMap ->
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(location))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
                }
            }
        )

    }





@Composable
fun ServicesSection(ListOfServices: List<String>) {
    Column(
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = "Types of services provided",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 9.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (service in ListOfServices) {
                ServiceItem(serviceName = service)
            }
        }
    }
}

@Composable
fun ServiceItem(serviceName: String) {
    Button(
        onClick = { /* Do something when clicked */ },
        shape = MaterialTheme.shapes.medium, // Rounded corners
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        modifier = Modifier
            .padding(4.dp)
    ) {
        Text(
            text = serviceName,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.Black
        )
    }
}


@Composable
fun Chip(label: String) {
    Card(
        backgroundColor = Color.LightGray,
        shape = RoundedCornerShape(50.dp), // Making it circular
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(8.dp),
            fontSize = 14.sp
        )
    }
}



@Composable
fun GroomerProfileCard(
    profilePic: String,
    name: String,
    distance: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(profilePic),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(55.dp)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2490DF)
                )
                Text(
                    text = "Pet Groomer",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.mipmap.locationnew1_foreground),  // Replace with your location icon
                    contentDescription = "Location",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = distance,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun SummarySection(summaryText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),// Add padding around the card
        // Subtle elevation for the card
        backgroundColor = Color.White  // Assuming a white background for the card
    ) {
        Column(
            modifier = Modifier.padding(16.dp)  // Padding inside the card
        ) {
            Text(
                text = "Summary",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))  // Space between the header and the body text
            Text(
                text = summaryText,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroomerProfile() {
    GroomerProfile(groomer = Groomer(
        name = "Will Parker",
        email = "will.parker@example.com",
        phoneNumber = "123-456-7890",
        address = Address("1234", "Street", "City", "State", LocationMap(0.0, 0.0, "Location")),
        yearsExperience = "5",
        services = listOf("Bathing and Drying", "Haircuts and Trimming", "Ear Cleaning", "Nail Trimming"),
        petTypes = listOf("Dogs", "Cats"),
        profilePic = "https://example.com/profile.jpg",
        price = 100
    )
    )
}
