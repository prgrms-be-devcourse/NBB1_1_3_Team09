package com.grepp.nbe1_3_team9.controller.location.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PlaceDetailResponse(
    @JsonProperty("placeId") val placeId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("latitude") val latitude: BigDecimal,
    @JsonProperty("longitude") val longitude: BigDecimal,
    @JsonProperty("formattedAddress") val formattedAddress: String,
    @JsonProperty("formattedPhoneNumber") val formattedPhoneNumber: String,
    @JsonProperty("photoUrl") val photoUrl: String,
    @JsonProperty("rating") val rating: Double,
    @JsonProperty("url") val url: String,
    @JsonProperty("weekdayText") val weekdayText: String,  // 요일 정보
    @JsonProperty("website") val website: String,
    @JsonProperty("openNow") val openNow: Boolean
)
