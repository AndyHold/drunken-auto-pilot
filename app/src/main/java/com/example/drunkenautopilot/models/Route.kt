package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "route", foreignKeys = [
    ForeignKey(entity = Episode::class,
        parentColumns = ["episode_id"],
        childColumns = ["episodeId"],
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Point::class,
        parentColumns = ["point_id"],
        childColumns = ["points"])
])
data class Route(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "route_id") val id: Long,
    val episodeId: Long,
    val points: List<Point>) {

    fun getLatLngs() : List<LatLng> {
        return points.map {
            it.getLatLng()
        }
    }
}