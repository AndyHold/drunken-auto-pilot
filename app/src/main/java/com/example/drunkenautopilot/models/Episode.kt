package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate

@Entity(tableName = "episode",
    foreignKeys = [
        ForeignKey(entity = AudioRecording::class,
            parentColumns = arrayOf("audio_recording_id"),
            childColumns = arrayOf("audioRecordings")),
        ForeignKey(entity = VideoRecording::class,
            parentColumns = arrayOf("video_recording_id"),
            childColumns = arrayOf("videoRecordings"))
    ])
data class Episode(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "episode_id") val id: Long,
    val steps: Int,
    val date: LocalDate,
    val route: List<LatLng> = ArrayList(),
    val videos: List<VideoRecording> = ArrayList(),
    val audioRecordings: List<AudioRecording> = ArrayList())