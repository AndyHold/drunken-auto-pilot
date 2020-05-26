package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.Point
import com.example.drunkenautopilot.models.PointDao
import com.example.drunkenautopilot.models.Route

class PointRepository(private val pointDao: PointDao, route: Route) {

    val getPointsForRoute: LiveData<List<Point>> = pointDao.getPointsForRoute(route.id)

    suspend fun insert(point: Point): Long {
        return pointDao.insert(point)
    }
}