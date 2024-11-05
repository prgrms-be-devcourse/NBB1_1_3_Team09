package com.grepp.nbe1_3_team9.controller.location.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.math.BigDecimal

data class PlaceRecommendResponse(
    @JsonProperty("placeId") val placeId: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("latitude") val latitude: BigDecimal,
    @JsonProperty("longitude") val longitude: BigDecimal,
    @JsonProperty("rating") val rating: Double,
    @JsonProperty("user_ratings_total") val userRatingsTotal: Int, // null 가능성을 고려하여 Int?로 선언
    @JsonProperty("address") val address: String,
    @JsonProperty("photoUrl") val photoUrl: String
) : Serializable

