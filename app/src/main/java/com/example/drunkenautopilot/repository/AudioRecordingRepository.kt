package com.example.drunkenautopilot.repository

import androidx.lifecycle.LiveData
import com.example.drunkenautopilot.models.AudioRecording
import com.example.drunkenautopilot.models.AudioRecordingDao

class AudioRecordingRepository(private val audioRecordingDao: AudioRecordingDao) {

    fun allAudioRecordings(episodeId: Long) : LiveData<List<AudioRecording>> {
        return audioRecordingDao.getLiveAudioRecordingsForEpisode(episodeId)
    }

    suspend fun insert(audioRecording: AudioRecording): Long {
        return audioRecordingDao.insertAudioRecording(audioRecording)
    }

    fun delete(audioRecordingId: Long) {
        audioRecordingDao.deleteAudioRecording(audioRecordingId)
    }

    suspend fun update(audioRecording: AudioRecording) {
        audioRecordingDao.updateAudioRecordings(audioRecording)
    }
}