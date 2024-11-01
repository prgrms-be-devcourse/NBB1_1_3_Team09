package com.grepp.nbe1_3_team9.controller.location.dto.api

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PlaceDetailApiResponse(val result: Result) {
    data class Result(
        val place_id: String,
        val name: String,
        val geometry: Geometry,
        val formatted_address: String,
        val formatted_phone_number: String = "",
        val photos: List<Photo> = emptyList(), // 기본값으로 빈 리스트 설정
        val rating: Double,
        val url: String= "",
        val website: String= "",
        val current_opening_hours: OpeningHours? = null
    )

    data class Geometry(val location: Location)

    data class Location(val lat: BigDecimal, val lng: BigDecimal)

    data class Photo(val photo_reference: String)

    data class OpeningHours(
        val open_now: Boolean,
        val weekday_text: List<String> = emptyList() // 빈 리스트로 기본값 설정
    )
}
