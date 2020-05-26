package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "route",
    foreignKeys = [
        ForeignKey(
            entity = Episode::class,
            parentColumns = ["episode_id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Route(
    var episodeId: Long = 0,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "route_id") var id: Long = 0
) {
}