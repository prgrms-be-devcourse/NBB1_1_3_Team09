package com.grepp.nbe1_3_team9.controller.location.dto.api

data class GeocodingApiResponse(
    val results: List<Result>
) {
    data class Result( val geometry: Geometry)

    data class Geometry(val location: Location)

    data class Location(val lat: Double, val lng: Double
    )
}