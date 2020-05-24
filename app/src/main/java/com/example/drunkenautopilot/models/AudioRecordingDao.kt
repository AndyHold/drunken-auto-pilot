package com.example.drunkenautopilot.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AudioRecordingDao {

    @Query("SELECT * FROM audio_recording WHERE episodeId = :episodeId")
    fun getLiveAudioRecordingsForEpisode(episodeId: Long): LiveData<List<AudioRecording>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAudioRecording(audioRecording: AudioRecording)

    @Query("DELETE FROM audio_recording WHERE audio_recording_id = :audioRecordingId")
    fun deleteAudioRecording(audioRecordingId: Long)

    @Update
    fun updateAudioRecordings(vararg audioRecordings: AudioRecording)
}