package com.example.drunkenautopilot

import android.location.Location
import com.example.drunkenautopilot.models.Point
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Common {

    companion object {
        fun getDistanceFromLatLonInMeters(firstPoint: Point, secondPoint: Point): Double {
            return getDistanceFromLatLonInMeters(firstPoint.latitude, firstPoint.longitude, secondPoint.latitude, secondPoint.longitude)
        }

        fun getDistanceFromLatLonInMeters(firstPoint: Location, secondPoint: LatLng): Double {
            return getDistanceFromLatLonInMeters(firstPoint.latitude, firstPoint.longitude, secondPoint.latitude, secondPoint.longitude)
        }

        private fun getDistanceFromLatLonInMeters(firstLat: Double, firstLon: Double, secondLat: Double, secondLon: Double): Double {
            val earthsRadius = 6371000
            val latDifference = degreesToRadians(firstLat - secondLat)
            val lonDifference = degreesToRadians(firstLon - secondLon)
            val alpha =
                sin(latDifference / 2) * sin(latDifference / 2) +
                        cos(degreesToRadians(firstLat)) * cos(
                    degreesToRadians(
                        secondLat
                    )
                ) *
                        sin(lonDifference / 2) * sin(lonDifference / 2)
            val c = 2 * atan2(sqrt(alpha), sqrt(1 - alpha))
            return earthsRadius * c
        }

        private fun degreesToRadians(deg: Double): Double {
            return deg * (Math.PI / 180)
        }
    }
}