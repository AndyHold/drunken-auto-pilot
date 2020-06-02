package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.models.EpisodeDao

class EpisodeRepository(private val episodeDao: EpisodeDao) {

    val allEpisodes = episodeDao.getAllEpisodes()

    val activeEpisode = episodeDao.getActiveEpisode()

    fun getSingleEpisode(episodeId: Long) : LiveData<Episode?> {
        return episodeDao.getSingleEpisode(episodeId)
    }

    suspend fun insert(episode: Episode): Long {
        return episodeDao.insert(episode)
    }

    fun delete(episodeId: Long) {
        episodeDao.deleteEpisode(episodeId)
    }

    suspend fun update(episode: Episode) {
        episodeDao.update(episode)
    }
}