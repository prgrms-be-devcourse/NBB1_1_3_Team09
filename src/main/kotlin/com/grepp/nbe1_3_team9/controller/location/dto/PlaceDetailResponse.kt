package com.grepp.nbe1_3_team9.controller.location.dto

import java.math.BigDecimal

data class PlaceDetailResponse(
    val placeId: String,
    val name: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val formattedAddress: String,
    val formattedPhoneNumber: String,
    val photoUrl: String,
    val rating: Double,
    val url: String,
    val weekdayText: String,  // 요일 정보
    val website: String,
    val openNow: Boolean  // 현재 영업 여부
)
