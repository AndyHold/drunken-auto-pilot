package com.example.drunkenautopilot.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RouteDao {

    @Query("SELECT * FROM route WHERE episodeId = :episodeId")
    fun getRoute(episodeId: Long): LiveData<Route>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(route: Route): Long

    @Update
    suspend fun update(route: Route)
}