package com.android.PetPamper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.PetPamper.model.Address
import com.android.PetPamper.model.LocationMap
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class AddressViewModel : ViewModel() {
  fun handleResponse(responseBody: String): List<Address> {
    val jsonArray = JSONObject(responseBody).getJSONArray("features")
    val addresses = mutableListOf<Address>()
    for (i in 0 until jsonArray.length()) {
      val gcGeometry = jsonArray.getJSONObject(i).getJSONObject("geometry")
      jsonArray.getJSONObject(i).getJSONObject("properties").getJSONObject("geocoding").let {
          jsonObj ->
        val gcLabel = jsonObj.optString("label", "")
        val gcName = jsonObj.optString("name", "")
        val gcHouse = jsonObj.optString("housenumber", "")
        val gcStreet = jsonObj.optString("street", "")
        val gcPostcode = jsonObj.optString("postcode", "")
        val gcCity = jsonObj.optString("city", "")
        val gcDistrict = jsonObj.optString("district", "")
        val gcCounty = jsonObj.optString("county", "")
        val gcState = jsonObj.optString("state", "")
        val gcCountry = jsonObj.optString("country", "")
        val gcCountryCode = jsonObj.optString("country_code", "")
        addresses.add(
            Address(
                gcStreet.plus(
                    if (gcHouse.isNotEmpty()) {
                      " $gcHouse"
                    } else {
                      ""
                    }),
                gcCity,
                gcState,
                gcPostcode,
                LocationMap(
                    latitude = gcGeometry.getJSONArray("coordinates").getDouble(1),
                    longitude = gcGeometry.getJSONArray("coordinates").getDouble(0),
                    name =
                        "${ if(gcName.isNotEmpty()){gcName.plus(": ")}else{""}}$gcStreet $gcHouse, ${if(gcState.isNotEmpty()){"$gcState, "}else if(gcCounty.isNotEmpty()){"$gcCounty, "}else if(gcDistrict.isNotEmpty()){"$gcDistrict, "}else{""} }$gcCountry (${gcCountryCode.uppercase()})")))
      }
    }
    return addresses
  }

  fun fetchAddresses(addressName: String, onResult: (List<Address>?) -> Unit) {
    viewModelScope.launch {
      try {
        val client = OkHttpClient()
        val url =
            "https://nominatim.openstreetmap.org/search?q=${addressName.replace(" ", "+")}&format=geocodejson&limit=5&addressdetails=1&extratags=1"
        val request = Request.Builder().url(url).build()

        val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }

        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string() ?: throw IOException("No response body")

        val addressList = handleResponse(responseBody)

        withContext(Dispatchers.Main) { onResult(addressList) }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) { onResult(null) }
      }
    }
  }
}
