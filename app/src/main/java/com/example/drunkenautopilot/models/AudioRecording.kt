package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "audio_recording",
    foreignKeys = [
        ForeignKey(entity = Episode::class,
            parentColumns = ["episode_id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class AudioRecording (
    var fileName: String,
    var episodeId: Long,
    var timeTaken: Timestamp = Timestamp(Date().time),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "audio_recording_id") var id: Long = 0)
