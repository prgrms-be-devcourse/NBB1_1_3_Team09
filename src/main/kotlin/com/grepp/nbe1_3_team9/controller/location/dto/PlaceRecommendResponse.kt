package com.grepp.nbe1_3_team9.controller.location.dto

import java.io.Serializable
import java.math.BigDecimal

data class PlaceRecommendResponse(
    val placeId: String,
    val name: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val rating: Double,
    val userRatingsTotal: Int,  // null 가능성을 고려하여 Int?로 선언
    val address: String,
    val photoUrl: String
) : Serializable
