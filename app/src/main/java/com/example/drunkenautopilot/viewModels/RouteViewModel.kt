package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import com.example.drunkenautopilot.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RouteRepository

    init {
        val routeDao = EpisodeRoomDatabase.getDatabase(
            application
        ).routeDao()
        repository = RouteRepository(
            routeDao
        )
    }

    fun getRoute(episodeId: Long): LiveData<Route> {
        return  repository.getRoute(episodeId)
    }

    fun insert(route: Route) = viewModelScope.launch(Dispatchers.IO) {
        route.id = repository.insert(route)
    }

    fun update(route: Route) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(route)
    }
}