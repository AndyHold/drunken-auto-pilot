package com.example.drunkenautopilot.repository

import com.example.drunkenautopilot.models.VideoRecording
import com.example.drunkenautopilot.models.VideoRecordingDao

class VideoRecordingRepository(private val videoRecordingDao: VideoRecordingDao, val episodeId: Long) {

    val allVideoRecordings = videoRecordingDao.getLiveVideoRecordingsForEpisode(episodeId)

    suspend fun insert(videoRecording: VideoRecording) {
        videoRecordingDao.insertVideoRecording(videoRecording)
    }

    fun delete(videoRecordingId: Long) {
        videoRecordingDao.deleteVideoRecording(videoRecordingId)
    }

    suspend fun update(videoRecording: VideoRecording) {
        videoRecordingDao.updateVideoRecordings(videoRecording)
    }
}