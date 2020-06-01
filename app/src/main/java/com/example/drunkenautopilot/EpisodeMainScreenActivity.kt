package com.example.drunkenautopilot

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.models.GoogleMapDTO
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.viewModels.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import org.json.JSONObject
import kotlin.properties.Delegates

const val PERMISSION_ID = 42

class EpisodeMainScreenActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    private var moving = false
    private var sensorManager: SensorManager? = null
    var episode: Episode? = null
    private lateinit var route: Route
    private lateinit var episodeViewModel: EpisodeViewModel
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var pointViewModel: PointViewModel
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var destination: Place
    private lateinit var settings: SharedPreferences
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var distanceTextView: TextView
    private lateinit var stepsTextView: TextView
    var currentLocation: Location? by Delegates.observable<Location?>(null) { property, oldValue, newValue ->
        currentLocation?.let {
            displayPoint()
            newCurrentLocation(it)
            pointViewModel.addPoint(it)

            if (getDistanceFromLatLonInMeters(currentLocation!!, destination.latLng!!) < 50.0) {
                // If you are near home
                episode?.isFinished = true
                episode?.let { it1 -> episodeViewModel.update(it1) }
                Toast.makeText(
                    applicationContext,
                    "You are home! Well Done!!!",
                    Toast.LENGTH_SHORT
                ).show()
                finish() // Return to home screen
            }

            Log.d(
                localClassName,
                "Location Updated: { latLng: { lat: ${it.latitude}, long: ${it.longitude} } }"
            )
        }
    }
    val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var directionsViewModel: DirectionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.episode_main_screen)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.openOptionsMenu()


        episodeViewModel = ViewModelProvider(this).get(EpisodeViewModel::class.java)

        episodeViewModel.activeEpisode.observe(this, Observer { activeEpisode ->
            if (activeEpisode != null) {
                println("HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!")
                episode = activeEpisode
                routeViewModel = ViewModelProvider(
                    this,
                    RouteViewModelFactory(
                        application,
                        episode!!
                    )
                ).get(RouteViewModel::class.java)
                routeViewModel.route.observe(this, Observer { currentRoute ->
                    route = currentRoute
                    pointViewModel = ViewModelProvider(
                        this,
                        PointViewModelFactory(
                            application,
                            route
                        )
                    ).get(PointViewModel::class.java)
                })
            } else if (episode != null) {
                episode = Episode()

                // Creates a new episode, route, and points view models along with
                // instances of episode and route.
                episode?.let { episodeInstance ->
                    episodeViewModel.insert(episodeInstance).invokeOnCompletion { episodeResult ->
                        if (episodeResult == null) {
                            Log.d(
                                localClassName,
                                "New Episode id is ${episode!!.id}"
                            )
                            routeViewModel = ViewModelProvider(
                                this,
                                RouteViewModelFactory(
                                    application,
                                    episode!!
                                )
                            ).get(RouteViewModel::class.java)

                            // Make a new route for this episode.
                            route = Route(episode!!.id)
                            routeViewModel.insert(route).invokeOnCompletion { routeResult ->
                                if (routeResult == null) {
                                    Log.d(
                                        localClassName,
                                        "New Route id is ${route.id}"
                                    )
                                    pointViewModel = ViewModelProvider(
                                        this,
                                        PointViewModelFactory(
                                            application,
                                            route
                                        )
                                    ).get(PointViewModel::class.java)

                                    getLastLocation()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to create new route",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e(
                                        localClassName,
                                        "Failed to create new route"
                                    )
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to create new episode",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                localClassName,
                                "Failed to create new episode"
                            )
                        }
                    }
                }
            }
        })


        distanceTextView = findViewById(R.id.distance_label)
        stepsTextView = findViewById(R.id.total_steps_label)

        val startAudioButton: ImageButton = findViewById(R.id.btn_start_audio)
        val startVideoButton: ImageButton = findViewById(R.id.btn_start_video)

        settings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

        val lat = settings.getFloat(resources.getString(R.string.address_latitude_key), 900f)
        val long = settings.getFloat(resources.getString(R.string.address_longitude_key), 900f)
        val name = settings.getString(resources.getString(R.string.address_name_key), null)

        Log.d(
            localClassName,
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

        mainHandler.post(object : Runnable {
            override fun run() {
                getLastLocation()
                mainHandler.postDelayed(this, 30000) // Updates location every 30 seconds.
            }
        })

        directionsViewModel =
            DirectionsViewModel(
                application,
                this
            )
    }

    fun updateRoute(response: String) {
        val responseAsJson = JSONObject(response)
        val status = responseAsJson.getString("status")
        if (status == "OK") {

            val result = ArrayList<List<LatLng>>()

            try {
                val responseObject = Gson().fromJson(response, GoogleMapDTO::class.java)
                val steps = responseObject.routes[0].legs[0].steps

                val path = ArrayList<LatLng>()
                for (i in steps.indices) {
                    val startLatLng = LatLng(
                        steps[i].end_location.lat.toDouble(),
                        steps[i].start_location.lng.toDouble()
                    )
                    path.add(startLatLng)

                    val endLatLng = LatLng(
                        steps[i].end_location.lat.toDouble(),
                        steps[i].start_location.lng.toDouble()
                    )
                    path.add(endLatLng)
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val route = responseAsJson.getJSONArray("routes").getJSONObject(0)
            val leg = route.getJSONArray("legs").getJSONObject(0)

            distanceTextView.text = leg.getJSONObject("distance").getString("text")

            val lineOptions = PolylineOptions()
            for (i in result.indices) {
                lineOptions.addAll(result[i])
                lineOptions.width(10f)
                lineOptions.color(Color.BLUE)
            }
            map.addPolyline(lineOptions)
        } else {
            Log.d(
                localClassName,
                response
            )
            Toast.makeText(
                this,
                "Could not reach directions API",
                Toast.LENGTH_SHORT
            ).show()
        }
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
    }

    private fun displayPoint() {
        if (currentLocation != null) {
            map.clear()
            map.addMarker(
                MarkerOptions().title("You are here")
                    .position(LatLng(currentLocation!!.latitude, currentLocation!!.longitude))
            )
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLocation!!.latitude, currentLocation!!.longitude),
                    14f
                )
            )
        }
    }

    private fun newCurrentLocation(place: Location) {
        destination.latLng?.let { destination ->
            directionsViewModel.getDirections(location = place, destination = destination)
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
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
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = mLastLocation
        }
    }

    override fun onResume() {
        super.onResume()
        moving = true
        val stepsSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepsSensor == null) {
            Toast.makeText(this, "No Step Counter Sensor !", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepsSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        moving = false
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (moving) {
            episode?.steps = event.values[0].toInt()
            episode?.let { episodeViewModel.update(it) }
            stepsTextView.text = event.values[0].toInt().toString()
        }
    }

    fun getDistanceFromLatLonInMeters(firstPoint: Location, secondPoint: LatLng): Double {
        val earthsRadius = 6371000
        val latDifference = degreesToRadons(firstPoint.latitude - secondPoint.latitude)
        val lonDifference = degreesToRadons(firstPoint.longitude - secondPoint.longitude)
        val alpha =
            Math.sin(latDifference / 2) * Math.sin(latDifference / 2) +
                    Math.cos(degreesToRadons(firstPoint.latitude)) * Math.cos(
                degreesToRadons(
                    secondPoint.latitude
                )
            ) *
                    Math.sin(lonDifference / 2) * Math.sin(lonDifference / 2)
        val c = 2 * Math.atan2(Math.sqrt(alpha), Math.sqrt(1 - alpha))
        val distance = earthsRadius * c
        return distance
    }

    fun degreesToRadons(deg: Double): Double {
        return deg * (Math.PI / 180)
    }
}