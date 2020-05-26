package com.example.drunkenautopilot.repository

import com.example.drunkenautopilot.models.Episode
import com.example.drunkenautopilot.models.EpisodeDao

class EpisodeRepository(private val episodeDao: EpisodeDao) {

    val allEpisodes = episodeDao.getAllEpisodes()

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