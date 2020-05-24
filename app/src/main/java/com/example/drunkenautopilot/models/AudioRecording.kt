package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.time.LocalDate

@Entity(tableName = "audio_recording",
    foreignKeys = [
        ForeignKey(entity = Episode::class,
            parentColumns = ["episode_id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class AudioRecording(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "audio_recording_id") val id: Long,
    val fileName: String,
    val timeTaken: Timestamp,
    val episodeId: Long)
