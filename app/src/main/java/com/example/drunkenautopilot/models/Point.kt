package com.example.drunkenautopilot.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.sql.Timestamp
import java.util.*

@Entity(tableName = "point",
    foreignKeys = [
    ForeignKey(entity = Route::class,
        parentColumns = ["route_id"],
        childColumns = ["routeId"],
        onDelete = ForeignKey.CASCADE)
    ])
data class Point(
    val routeId: Long,
    val lat: Double,
    val long: Double,
    val timeTaken: Timestamp = Timestamp(Date().time),
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "point_id") var id: Long = 0
) {
    fun getLatLng(): LatLng {
        return LatLng(lat, long)
    }
}