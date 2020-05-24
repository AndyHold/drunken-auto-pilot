package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "video_recording",
    foreignKeys = [
        ForeignKey(entity = Episode::class,
            parentColumns = ["episode_id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class VideoRecording(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "video_recording_id") val id: Long,
    val episodeId: Long,
    val timeTaken: Timestamp,
    val fileName: String
    )