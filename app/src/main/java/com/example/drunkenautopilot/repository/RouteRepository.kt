package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.Route
import com.example.drunkenautopilot.models.RouteDao

class RouteRepository(private val routeDao: RouteDao) {

    fun getRoute(episodeId: Long): LiveData<Route> {
        return routeDao.getRoute(episodeId)
    }

    suspend fun insert(route: Route): Long {
        return routeDao.insert(route)
    }

    suspend fun update(route: Route) {
        routeDao.update(route)
    }
}