package com.example.drunkenautopilot

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.drunkenautopilot.models.AudioRecording
import com.example.drunkenautopilot.viewModels.AudioRecordingViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drunkenautopilot.audioRecordingsRecyclerView.AudioRecordingAdapter
import com.example.drunkenautopilot.models.Point
import com.example.drunkenautopilot.viewModels.EpisodeViewModel
import com.example.drunkenautopilot.viewModels.PointViewModel
import com.example.drunkenautopilot.viewModels.RouteViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.io.File
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds


class ViewEpisodeActivity : AppCompatActivity(), OnMapReadyCallback {

    private var episodeId: Long = 0
    private lateinit var tvDistance: TextView
    private lateinit var tvSteps: TextView
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var rvAudioRecordings: RecyclerView
    private lateinit var audioRecordingViewModel: AudioRecordingViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var recordings: MutableList<AudioRecording> = mutableListOf()
        set(value) {
            field = value
            rvAudioRecordings.adapter = AudioRecordingAdapter(this, field) { audioRecording ->
                val fileLocation =
                    Environment.getExternalStorageDirectory().absolutePath + "/${audioRecording.fileName}"
                val file = File(fileLocation)
                val uri: Uri = Uri.fromFile(file)
                mediaPlayer = MediaPlayer.create(this, uri)
                mediaPlayer?.setOnPreparedListener { mp ->
                    mp.start()
                    mp.setOnCompletionListener {
                        mp.release()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_episode_activity)

        episodeId = intent.getLongExtra("episode_id", 0)
        if (episodeId <= 0) {
            Toast.makeText(
                this,
                getString(R.string.cannot_find_episode),
                Toast.LENGTH_SHORT
            ).show()
        }

        tvDistance = findViewById(R.id.tv_total_distance)
        tvSteps = findViewById(R.id.tv_total_steps)

        mapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        rvAudioRecordings = findViewById(R.id.rv_audio_recordings)
        val layoutManager = LinearLayoutManager(this)
        rvAudioRecordings.layoutManager = layoutManager
        val decoration = DividerItemDecoration(this, layoutManager.orientation)
        rvAudioRecordings.addItemDecoration(decoration)

        val episodeViewModel = ViewModelProvider(this).get(EpisodeViewModel::class.java)
        episodeViewModel.getSingleEpisode(episodeId).observe(this, Observer {
            if (it != null) {
                tvSteps.text = it.steps.toString()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.could_not_find_episode),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val routeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)
        routeViewModel.getRoute(episodeId).observe(this, Observer {
            val routeId = it.id
            val pointViewModel = ViewModelProvider(this).get(PointViewModel::class.java)

            pointViewModel.getPoints(routeId).observe(this, Observer { points ->
                updateRoute(points)
                tvDistance.text = getString(R.string.distance_display_template).format(getTotalDistance(points) / 1000.0)
            })
        })

        audioRecordingViewModel = ViewModelProvider(this).get(AudioRecordingViewModel::class.java)

        audioRecordingViewModel.getAudioRecordings(episodeId).observe(this, Observer {
            recordings = it.toMutableList()
            Log.d(
                localClassName,
                "AudioRecordings has ${it.size} items"
            )
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    fun updateRoute(points: List<Point>) {
        val route =
            points.map { point ->
                LatLng(point.latitude, point.longitude)
            }

        val lineOptions = PolylineOptions()

        lineOptions.addAll(route)
        lineOptions.width(10f)
        lineOptions.color(Color.BLUE)
        map.addPolyline(lineOptions)
        zoomToRoute(route)
    }

    fun getTotalDistance(points: List<Point>): Double {
        var result = 0.0

        if (!points.isEmpty()) {
            var currentPoint = points[0]
            var currentIndex = 1

            while (currentIndex < points.size) {
                result += Common.getDistanceFromLatLonInMeters(currentPoint, points[currentIndex])
                currentPoint = points[currentIndex]
                currentIndex++
            }
        }

        return result
    }

    fun zoomToRoute(lstLatLngRoute: List<LatLng>?) {
        if (lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()

        for (latLngPoint in lstLatLngRoute)
            boundsBuilder.include(latLngPoint)

        val routePadding = 70
        val latLngBounds = boundsBuilder.build()

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding))
    }
}