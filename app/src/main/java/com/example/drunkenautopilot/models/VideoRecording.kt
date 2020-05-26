package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "video_recording",
    foreignKeys = [
        ForeignKey(entity = Episode::class,
            parentColumns = ["episode_id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class VideoRecording(
    val episodeId: Long,
    val fileName: String,
    val timeTaken: Timestamp = Timestamp(Date().time),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "video_recording_id") var id: Long = 0
    )