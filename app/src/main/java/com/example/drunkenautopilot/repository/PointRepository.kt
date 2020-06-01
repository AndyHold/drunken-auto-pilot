package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.Point
import com.example.drunkenautopilot.models.PointDao

class PointRepository(private val pointDao: PointDao) {

    suspend fun insert(point: Point): Long {
        return pointDao.insert(point)
    }

    fun getPointsForRoute(routeId: Long): LiveData<List<Point>> {
        return pointDao.getPointsForRoute(routeId)
    }
}