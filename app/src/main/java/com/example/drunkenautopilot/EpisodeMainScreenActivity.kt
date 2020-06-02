package com.example.drunkenautopilot

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaRecorder
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.drunkenautopilot.models.AudioRecording
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
import java.io.File
import java.io.IOException
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

const val LOCATION_PERMISSION_ID = 42
const val RECORDING_PERMISSION_ID = 42

class EpisodeMainScreenActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
    // Audio recorder stuff
    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var recording: Boolean = false
    private var audioFilename = ""
    //----------------------
    private var moving = false
    private var sensorManager: SensorManager? = null
    var episode: Episode? = null
    private var route: Route? = null
    private lateinit var episodeViewModel: EpisodeViewModel
    private var routeViewModel: RouteViewModel? = null
    private var pointViewModel: PointViewModel? = null
    private var audioRecordingViewModel: AudioRecordingViewModel? = null
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
            pointViewModel?.let { pvm ->
                if (route != null) {
                    pvm.addPoint(it, route!!.id)
                }
            }

            if (Common.getDistanceFromLatLonInMeters(currentLocation!!, destination.latLng!!) < 50.0) {
                // If you are near home
                episode?.isFinished = true
                episode?.let { it1 -> episodeViewModel.update(it1) }
                mainHandler.removeCallbacksAndMessages(null) // stop all future location updates

                createNotification()

                Toast.makeText(
                    applicationContext,
                    getString(R.string.notification_title),
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
    // Notifications stuff
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.episode_main_screen)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setSupportActionBar(findViewById(R.id.toolbar))


        notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager


        val intent = Intent(this, ViewEpisodeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId, description, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        } else {

            builder = Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_content))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        supportActionBar?.openOptionsMenu()

        episodeViewModel = ViewModelProvider(this).get(EpisodeViewModel::class.java)
        audioRecordingViewModel = ViewModelProvider(this).get(AudioRecordingViewModel::class.java)

        episodeViewModel.activeEpisode.observe(this, Observer { activeEpisode ->
            if (activeEpisode != null) {
                if (episode != null && episode?.id != activeEpisode.id) {
                    episodeViewModel.delete(episode!!.id)
                }
                episode = activeEpisode
                routeViewModel = ViewModelProvider(
                    this
                ).get(RouteViewModel::class.java)
                routeViewModel?.getRoute(activeEpisode.id)?.observe(this, Observer { currentRoute ->
                    if (currentRoute != null) {
                        route = currentRoute
                        pointViewModel = ViewModelProvider(
                            this
                        ).get(PointViewModel::class.java)
                    }
                })
            } else if (episode == null) {
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
                                this
                            ).get(RouteViewModel::class.java)

                            // Make a new route for this episode.
                            route = Route(episode!!.id)
                            routeViewModel?.insert(route!!)?.invokeOnCompletion { routeResult ->
                                if (routeResult == null) {
                                    Log.d(
                                        localClassName,
                                        "New Route id is ${route!!.id}"
                                    )
                                    pointViewModel = ViewModelProvider(
                                        this
                                    ).get(PointViewModel::class.java)

                                    getLastLocation()
                                } else {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.route_creation_failure),
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
                                getString(R.string.episode_creation_failure),
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
        val cancelButton: Button = findViewById(R.id.btn_cancel)

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
            if (!checkAudioRecordPersmissions()) {
                requestAudioRecordPermissions()
            } else {
                startRecording()
            }
        }

        cancelButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.cancel_episode_title)
                .setMessage(R.string.cancel_episode_content)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes_cancel) { _, _ ->
                    finish()
                    mainHandler.removeCallbacksAndMessages(null) // stop all future location updates
                    route =
                        null // required for when the episode is cancelled just as the location is updated.
                    episodeViewModel.delete(episode!!.id)
                }
                .setNegativeButton(R.string.no_go_home, null)
                .show()
        }

        mapFragment.getMapAsync(this)

        if (!checkLocationPermissions()) {
            requestLocationPermissions()
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

    private fun createNotification() {
        notificationManager.notify(1234, builder.build())
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
                getString(R.string.directions_api_failure),
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
                MarkerOptions().title(getString(R.string.marker_title))
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

    private fun checkLocationPermissions(): Boolean {
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

    private fun checkAudioRecordPersmissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSION_ID
        )
    }

    private fun requestAudioRecordPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            RECORDING_PERMISSION_ID
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
        if (checkLocationPermissions()) {
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
                Toast.makeText(this, getString(R.string.turn_on_locations), Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestLocationPermissions()
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
            stepsTextView.text = getString(R.string.no_step_counter)
            Toast.makeText(this, getString(R.string.no_step_counter), Toast.LENGTH_SHORT).show()
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

    private fun startRecording() {
        // Start audio recording here
        audioFilename = "${Date().time}.mp3"
        output = Environment.getExternalStorageDirectory().absolutePath + "/${audioFilename}"

        File(output!!).parentFile?.mkdirs()

        mediaRecorder = MediaRecorder()

        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setOutputFile(output)

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            recording = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.recording_title)
            .setMessage(R.string.recording_message)
            .setIcon(R.drawable.ic_mic_black_24dp)
            .setPositiveButton(R.string.stop_recording) { _, _ ->
                stopRecording()

                val audioRecording = AudioRecording(audioFilename, episode!!.id)

                audioRecordingViewModel?.insert(audioRecording)?.invokeOnCompletion { routeResult ->
                    if (routeResult == null) {
                        Log.d(
                            localClassName,
                            "New AudioRecording id is ${audioRecording.id}"
                        )
                    }
                }

                Toast.makeText(
                    applicationContext,
                    getString(R.string.recording_saved),
                    Toast.LENGTH_SHORT
                ).show()
                audioFilename = ""
            }
            .setNegativeButton(R.string.cancel_recording) { _, _ ->
                stopRecording()

                val file =
                    File(Environment.getExternalStorageDirectory().absolutePath + "/$audioFilename")
                if (file.delete()) {
                    Log.d(
                        localClassName,
                        "Audio file deleted: $audioFilename"
                    )
                }

                Toast.makeText(
                    applicationContext,
                    getString(R.string.recording_cancelled),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }

    private fun stopRecording() {
        if (recording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            recording = false
        }
    }
}