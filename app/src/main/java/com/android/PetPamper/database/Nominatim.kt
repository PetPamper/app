import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.PetPamper.model.LocationMap
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class LocationViewModel : ViewModel() {

  var locationmap: LocationMap? = null

  private fun handleResponse(responseBody: String): List<LocationMap> {
    val jsonArray = JSONArray(responseBody)
    val locations = mutableListOf<LocationMap>()
    for (i in 0 until jsonArray.length()) {
      jsonArray.getJSONObject(i).let { jsonObj ->
        locations.add(
            LocationMap(
                latitude = jsonObj.optString("lat", "0.0").toDouble(),
                longitude = jsonObj.optString("lon", "0.0").toDouble(),
                name = jsonObj.optString("display_name", "Unknown Location")))
      }
    }
    return locations
  }

  fun fetchLocation(locationName: String, onResult: (List<LocationMap>?) -> Unit) {
    viewModelScope.launch {
      try {
        val client = OkHttpClient()
        val url =
            "https://nominatim.openstreetmap.org/search?q=${locationName.replace(" ", "+")}&format=json&limit=1"
        val request = Request.Builder().url(url).build()

        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string() ?: throw IOException("No response body")

        val locations = handleResponse(responseBody)

        withContext(Dispatchers.Main) {
          onResult(locations)
          Log.d("Location Fetch", "Locations fetched successfully: $locations")
        }
      } catch (e: Exception) {

        withContext(Dispatchers.Main) {
          onResult(null)
          Log.e("Location Fetch", "Failed to fetch locations", e)
        }
      }
    }
  }
}
