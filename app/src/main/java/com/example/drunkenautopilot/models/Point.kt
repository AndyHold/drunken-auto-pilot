package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Timestamp
import java.util.*

@Entity(
    tableName = "point",
    foreignKeys = [
        ForeignKey(
            entity = Route::class,
            parentColumns = ["route_id"],
            childColumns = ["routeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Point(
    var latitude: Double,
    var longitude: Double,
    var routeId: Long,
    var timeTaken: Timestamp = Timestamp(Date().time),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "point_id") var id: Long = 0
)