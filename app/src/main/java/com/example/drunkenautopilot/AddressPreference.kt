package com.example.drunkenautopilot

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class AddressPreference : FragmentActivity(), OnMapReadyCallback {

    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    lateinit var autoCompleteFragment: AutocompleteSupportFragment
    var currentAddress: Place? = null
    lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.address_preference)

        Places.initialize(applicationContext, resources.getString(R.string.map_key))
        settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        val submitButton: Button = findViewById(R.id.submit_address_button)

        submitButton.setOnClickListener {
            if (currentAddress != null) {
                saveAddress(currentAddress!!)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Invalid Address, Please Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Initialize the AutocompleteSupportFragment.
        autoCompleteFragment =
            supportFragmentManager.findFragmentById(R.id.address_auto_complete) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autoCompleteFragment.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                displayPoint(place.latLng!!, place.name!!)

                currentAddress = place

                saveAddress(place)
                Log.d(
                    localClassName,
                    "Place: { address: ${place.name}, latLng: ${place.latLng} }"
                )
            }

            override fun onError(status: Status) {
                Log.e(
                    localClassName,
                    "An error occurred: $status"
                )
            }
        })

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lat = settings.getFloat(resources.getString(R.string.address_latitude_key), 900f)
        val long = settings.getFloat(resources.getString(R.string.address_longitude_key), 900f)
        val name = settings.getString(resources.getString(R.string.address_name_key), null)
        if (name != null) {
            currentAddress = Place.builder()
                .setName(name)
                .setLatLng(
                    LatLng(
                        lat.toDouble(),
                        long.toDouble()
                    )
                )
                .build()
        }


        Log.d(
            localClassName,
            "Address: { name: $name, latLng: { lat: $lat, long: $long } }"
        )

        if (lat <= 90 && long <= 180 && name != null) {
            displayPoint(LatLng(lat.toDouble(), long.toDouble()), name)
        }
    }

    private fun displayPoint(latLng: LatLng, name: String) {
        map.clear()
        map.addMarker(MarkerOptions().title(name).position(latLng))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(latLng.latitude, latLng.longitude),
                14f
            )
        )
    }

    private fun saveAddress(place: Place) {
        val editor = settings.edit()

        editor.putFloat(
            resources.getString(R.string.address_latitude_key),
            place.latLng!!.latitude.toFloat()
        )
        editor.putFloat(
            resources.getString(R.string.address_longitude_key),
            place.latLng!!.longitude.toFloat()
        )
        editor.putString(
            resources.getString(R.string.address_name_key),
            place.name
        )
        editor.apply()
    }
}