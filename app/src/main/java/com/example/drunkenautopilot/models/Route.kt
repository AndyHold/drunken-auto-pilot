package com.example.drunkenautopilot.models

import android.location.Location
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
    var episodeId: Long = 0,
    val points: ArrayList<Point> = ArrayList(),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "route_id") var id: Long = 0) {

    fun getLatLngs() : List<LatLng> {
        return points.map {
            it.getLatLng()
        }
    }

    fun addPoint(location: Location) {
        points.add(Point(id, location.latitude, location.longitude))
    }
}