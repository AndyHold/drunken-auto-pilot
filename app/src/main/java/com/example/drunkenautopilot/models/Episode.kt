package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "episode")
data class Episode(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "episode_id") val id: Long,
    val steps: Int,
    val date: Timestamp)