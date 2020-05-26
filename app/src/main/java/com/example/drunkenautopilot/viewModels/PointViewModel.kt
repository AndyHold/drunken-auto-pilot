package com.example.drunkenautopilot.viewModels

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Point
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import com.example.drunkenautopilot.repository.PointRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PointViewModel(application: Application, val route: Route) : AndroidViewModel(application) {

    private val repository: PointRepository
    val points: LiveData<List<Point>>

    init {
        val pointDao = EpisodeRoomDatabase.getDatabase(
            application
        ).pointDao()
        repository = PointRepository(
            pointDao,
            route
        )
        points = repository.getPointsForRoute
    }

    fun addPoint(location: Location) {
        val point = Point(location.latitude, location.longitude, route.id)
        insert(point).invokeOnCompletion {
            Log.d(
                "DrunkenAutoPilot",
                "New Point id is ${point.id}"
            )
        }
    }

    private fun insert(point: Point) = viewModelScope.launch(Dispatchers.IO) {
        point.id = repository.insert(point)
    }

    fun getLatLngs() : List<LatLng> {
        return points.value?.map {
            LatLng(it.latitude, it.longitude)
        }!!
    }
}