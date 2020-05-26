package com.example.drunkenautopilot.models

data class GoogleMapDTO(
    var routes: List<Routes> = ArrayList()
)


data class Routes(
    var legs: List<Legs> = ArrayList()
)


data class Legs(
    var distance: Distance = Distance(),
    var duration: Duration = Duration(),
    var end_address: String = "",
    var start_address: String = "",
    var end_location: Location = Location(),
    var start_location: Location = Location(),
    var steps: List<Steps> = ArrayList()
)

data class Steps(
    var distance: Distance = Distance(),
    var duration: Duration = Duration(),
    var end_address: String = "",
    var start_address: String = "",
    var end_location: Location = Location(),
    var start_location: Location = Location(),
    var polyline: PolyLine = PolyLine(),
    var travel_mode: String = "",
    var maneuver: String = ""
)

data class Duration(
    var text: String = "",
    var value: Long = 0
)

data class Distance(
    var text: String = "",
    var value: Long = 0
)

data class PolyLine(
    var points: String = ""
)

data class Location(
    var lat: String = "",
    var lng: String = ""
)
