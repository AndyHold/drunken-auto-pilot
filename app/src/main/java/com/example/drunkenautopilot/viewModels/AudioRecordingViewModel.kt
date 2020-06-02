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

class AudioRecordingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AudioRecordingRepository

    init {
        val audioRecordingDao = EpisodeRoomDatabase.getDatabase(
            application
        ).audioRecordingDao()
        repository =
            AudioRecordingRepository (
                audioRecordingDao
            )
    }

    fun getAudioRecordings(episodeId: Long): LiveData<List<AudioRecording>> {
        return repository.allAudioRecordings(episodeId)
    }

    fun insert(audioRecording: AudioRecording) = viewModelScope.launch(Dispatchers.IO) {
        audioRecording.id = repository.insert(audioRecording)
    }

    fun update(audioRecording: AudioRecording) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(audioRecording)
    }

    fun delete(audioRecordingId: Long) {
        repository.delete(audioRecordingId)
    }
}