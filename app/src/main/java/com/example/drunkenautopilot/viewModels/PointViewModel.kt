package com.example.drunkenautopilot.viewModels

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Point
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import com.example.drunkenautopilot.repository.PointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PointViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PointRepository

    init {
        val pointDao = EpisodeRoomDatabase.getDatabase(
            application
        ).pointDao()
        repository = PointRepository(
            pointDao
        )
    }

    fun getPoints(routeId: Long): LiveData<List<Point>> {
        return repository.getPointsForRoute(routeId)
    }

    fun addPoint(location: Location, routeId: Long) {
        val point = Point(location.latitude, location.longitude, routeId)
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
}