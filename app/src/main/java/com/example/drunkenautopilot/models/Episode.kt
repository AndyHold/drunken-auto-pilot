package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "episode")
data class Episode(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "episode_id") var id: Long = 0,
    val steps: Int = 0,
    val date: Timestamp = Timestamp(Date().time))