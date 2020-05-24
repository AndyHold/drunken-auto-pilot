package com.example.drunkenautopilot.repository

import com.example.drunkenautopilot.models.AudioRecording
import com.example.drunkenautopilot.models.AudioRecordingDao

class AudioRecordingRepository(private val audioRecordingDao: AudioRecordingDao, val episodeId: Long) {

    val allAudioRecordings = audioRecordingDao.getLiveAudioRecordingsForEpisode(episodeId)

    suspend fun insert(audioRecording: AudioRecording) {
        audioRecordingDao.insertAudioRecording(audioRecording)
    }

    fun delete(audioRecordingId: Long) {
        audioRecordingDao.deleteAudioRecording(audioRecordingId)
    }

    suspend fun update(audioRecording: AudioRecording) {
        audioRecordingDao.updateAudioRecordings(audioRecording)
    }
}