package com.grepp.nbe1_3_team9.controller.location.dto.api

import com.grepp.nbe1_3_team9.controller.location.dto.api.PlaceDetailApiResponse.Photo
import java.math.BigDecimal

data class GooglePlacesNearbyResponse(
    val results: List<Result>
) {
    data class Result(
        val place_id: String,
        val name: String,
        val rating: Double?,
        val user_ratings_total: Int,
        val vicinity: String = "",
        val photos: List<Photo> = emptyList(),
        val geometry: Geometry
    )

    data class Geometry(  val location: Location)

    data class Location(val lat: BigDecimal,val lng: BigDecimal)

    data class Photo(val photo_reference: String)
}