package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.models.RouteDao

class RouteRepository(private val routeDao: RouteDao, private val episode: Episode) {

    val getRoute: LiveData<Route> = routeDao.getRoute(episode.id)

    suspend fun insert(route: Route): Long {
        route.episodeId = episode.id
        return routeDao.insert(route)
    }

    suspend fun update(route: Route) {
        routeDao.update(route)
    }
}