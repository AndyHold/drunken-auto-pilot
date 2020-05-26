package com.example.drunkenautopilot.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drunkenautopilot.models.AudioRecording
import com.example.drunkenautopilot.repository.AudioRecordingRepository
import com.example.drunkenautopilot.repository.EpisodeRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AudioRecordingViewModel(application: Application, episodeId: Long) : AndroidViewModel(application) {

    private val repository: AudioRecordingRepository
    val audioRecordings: LiveData<List<AudioRecording>>

    init {
        val audioRecordingDao = EpisodeRoomDatabase.getDatabase(
            application
        ).audioRecordingDao()
        repository =
            AudioRecordingRepository(
                audioRecordingDao,
                episodeId
            )
        audioRecordings = repository.allAudioRecordings
    }

    fun insert(audioRecording: AudioRecording) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(audioRecording)
    }

    fun update(audioRecording: AudioRecording) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(audioRecording)
    }

    fun delete(audioRecordingId: Long) {
        repository.delete(audioRecordingId)
    }
}