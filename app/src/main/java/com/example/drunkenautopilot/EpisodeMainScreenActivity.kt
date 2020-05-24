package com.example.drunkenautopilot

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import kotlin.properties.Delegates


class EpisodeMainScreenActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var map: GoogleMap
    lateinit var mapFragment: SupportMapFragment
    lateinit var destination: Place
    lateinit var settings: SharedPreferences
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val PERMISSION_ID = 42
    var currentLocation: Location? by Delegates.observable<Location?>(null) { property, oldValue, newValue ->
        currentLocation?.let {
            newCurrentLocation(Place.builder().setName("here").setLatLng(LatLng(it.latitude, it.longitude)).build())
            Log.d(
                "DrunkenAutoPilot",
                "Location Updated: { latLng: { lat: ${it.latitude}, long: ${it.longitude} } }"
            )
        }
    }
    val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.episode_main_screen)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.openOptionsMenu()
        val startAudioButton: ImageButton = findViewById(R.id.btn_start_audio)
        val startVideoButton: ImageButton = findViewById(R.id.btn_start_video)

        settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        val lat = settings.getFloat(resources.getString(R.string.address_latitude_key), 900f)
        val long = settings.getFloat(resources.getString(R.string.address_longitude_key), 900f)
        val name = settings.getString(resources.getString(R.string.address_name_key), null)

        Log.d(
            "DrunkenAutoPilot",
            "Address Loaded: { name: $name, latLng: { lat: $lat, long: $long } }"
        )
        if (name != null) {
            destination = Place.builder()
                .setName(name)
                .setLatLng(
                    LatLng(
                        lat.toDouble(),
                        long.toDouble()
                    )
                )
                .build()
        }

        startAudioButton.setOnClickListener {
            // TODO: Start an Audio Recording
        }

        startVideoButton.setOnClickListener {
            // TODO: Start a Video Recording
        }

        mapFragment.getMapAsync(this)

        if (!checkPermissions()) {
            requestPermissions()
        }

        getLastLocation()
        mainHandler.post(object : Runnable {
            override fun run() {
                getLastLocation()
                mainHandler.postDelayed(this, 10000)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (destination.name != null) {
            displayPoint(destination)
        }
    }

    private fun displayPoint(place: Place) {
        map.clear()
        map.addMarker(
            MarkerOptions().title(place.name).position(place.latLng!!)
        )
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(place.latLng!!.latitude, place.latLng!!.longitude),
                14f
            )
        )
    }

    private fun newCurrentLocation(place: Place) {
//        TODO: Make a route home in here...
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = location
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            currentLocation = mLastLocation
        }
    }
}