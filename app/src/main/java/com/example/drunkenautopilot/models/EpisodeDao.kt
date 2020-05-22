package com.example.drunkenautopilot.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeDao {

    @Query("SELECT * FROM episode ORDER BY date ASC")
    fun getAllEpisodes(): List<Episode>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(episode: Episode): Long

    @Query("DELETE FROM episode WHERE episode_id = :episodeId")
    fun deleteEpisode(episodeId: Long)
}