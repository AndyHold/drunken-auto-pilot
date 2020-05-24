package com.example.drunkenautopilot.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.VideoRecording
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoRecordingViewModel(application: Application, episodeId: Long) : AndroidViewModel(application) {

    private val repository: VideoRecordingRepository
    val videoRecordings: LiveData<List<VideoRecording>>

    init {
        val videoRecordingDao = EpisodeRoomDatabase.getDatabase(application).videoRecordingDao()
        repository = VideoRecordingRepository(videoRecordingDao, episodeId)
        videoRecordings = repository.allVideoRecordings
    }

    fun insert(videoRecording: VideoRecording) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(videoRecording)
    }

    fun update(videoRecording: VideoRecording) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(videoRecording)
    }

    fun delete(videoRecordingId: Long) {
        repository.delete(videoRecordingId)
    }
}