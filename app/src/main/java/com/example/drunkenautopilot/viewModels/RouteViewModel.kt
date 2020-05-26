package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import com.example.drunkenautopilot.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouteViewModel(application: Application, episode: Episode) : AndroidViewModel(application) {

    private val repository: RouteRepository
    val route: LiveData<Route>

    init {
        val routeDao = EpisodeRoomDatabase.getDatabase(
            application
        ).routeDao()
        repository = RouteRepository(
            routeDao,
            episode
        )
        route = repository.getRoute
    }

    fun insert(route: Route) = viewModelScope.launch(Dispatchers.IO) {
        route.id = repository.insert(route)
    }

    fun update(route: Route) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(route)
    }
}