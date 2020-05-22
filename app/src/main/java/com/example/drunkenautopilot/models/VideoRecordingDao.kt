package com.example.drunkenautopilot.models

import androidx.room.*

@Dao
interface VideoRecordingDao {

    @Query("SELECT * FROM video_recording WHERE episodeId = :episodeId")
    fun getVideoRecordingsForEpisode(episodeId: Long): List<VideoRecording>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideoRecording(videoRecording: VideoRecording): Long

    @Query("DELETE FROM video_recording WHERE video_recording_id = :videoRecordingId")
    fun deleteVideo(videoRecordingId: Long)

    @Update
    fun updateVideoRecordings(vararg videoRecordings: VideoRecording)
}