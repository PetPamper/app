import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.android.PetPamper.R
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.Groomer
import com.android.PetPamper.model.LocationMap

@Composable
fun GroomerProfile(groomer: Groomer) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(groomer.profilePic),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoCard( title = "Groomable Pets", description = "Dogs, Cats")
            InfoCard( title = "Experience years", description = "4")
            InfoCard( title = "Location", description = "Ecublens, VD")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = groomer.name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Black
        )
        Text(
            text = "Experience: ${groomer.yearsExperience} years",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            groomer.services.forEach { service ->
                Chip(label = service)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Provided Services",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        groomer.services.forEach { service ->
            Text(text = service, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Dummy map image, replace with actual map view if needed
//        Image(
//            painter = painterResource(id = R.mipmap.petpamper_logo_round),
//            contentDescription = "Map Location",
//            modifier = Modifier.fillMaxWidth().height(150.dp),
//            contentScale = ContentScale.FillBounds
//        )
    }
}

@Composable
fun InfoCard( title: String, description: String) {
    Card(
        modifier = Modifier.padding(2.dp).width(110.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            Text(text = description, fontSize = 16.sp, color = Color.Gray)
        }
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
