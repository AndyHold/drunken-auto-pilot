package com.example.drunkenautopilot.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EpisodeDao {

    @Query("SELECT * FROM episode ORDER BY date ASC")
    fun getAllEpisodes(): LiveData<List<Episode>>

    @Query("SELECT * FROM episode WHERE isFinished = 0")
    fun getActiveEpisode(): LiveData<Episode?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(episode: Episode): Long

    @Query("DELETE FROM episode WHERE episode_id = :episodeId")
    fun deleteEpisode(episodeId: Long)

    @Update
    suspend fun update(episode: Episode)
}