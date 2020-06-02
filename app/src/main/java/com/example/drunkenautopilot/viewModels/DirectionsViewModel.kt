package com.example.drunkenautopilot.viewModels

import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.EpisodeMainScreenActivity
import com.example.drunkenautopilot.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class DirectionsViewModel(application: Application, private val screen: EpisodeMainScreenActivity) : AndroidViewModel(application) {

    fun getDirections(location: Location, destination: LatLng) {
        val parameters = mapOf(
            "key" to getApplication<Application>().getString(R.string.google_api_key),
            "origin" to "${location.latitude},${location.longitude}",
            "destination" to "${destination.latitude},${destination.longitude}",
            "mode" to "walking",
            "language" to getApplication<Application>().getString(R.string.language),
            "units" to "metric")
        val url = parameterizeUrl("https://maps.googleapis.com/maps/api/directions/json", parameters)

        viewModelScope.launch {
            val response = makeRequest(url)

            screen.updateRoute(response)
        }
    }

    private fun parameterizeUrl(url: String, parameters: Map<String, String>): URL {
        val builder = Uri.parse(url).buildUpon()
        parameters.forEach { key, value -> builder.appendQueryParameter(key, value) }
        val uri = builder.build()
        return URL(uri.toString())
    }

    private suspend fun makeRequest(url: URL): String {
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpsURLConnection
            val buffer = BufferedInputStream(connection.inputStream)
            try {
                buffer.readBytes().toString(Charset.defaultCharset())
            } finally {
                connection.disconnect()
                buffer.close()
            }
        }
    }
}