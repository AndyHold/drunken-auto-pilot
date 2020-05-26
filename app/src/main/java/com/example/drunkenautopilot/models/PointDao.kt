package com.example.drunkenautopilot.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PointDao {
    @Query("SELECT * FROM point WHERE routeId = :routeId ORDER BY timeTaken ASC")
    fun getPointsForRoute(routeId: Long): LiveData<List<Point>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(point: Point): Long
}