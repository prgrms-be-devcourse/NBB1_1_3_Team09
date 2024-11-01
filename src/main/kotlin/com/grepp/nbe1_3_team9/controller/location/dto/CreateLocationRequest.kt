package com.grepp.nbe1_3_team9.controller.location.dto

import com.grepp.nbe1_3_team9.domain.entity.Location
import java.math.BigDecimal

data class CreateLocationRequest(
    val placeId: String,
    val placeName: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val address: String,
    val rating: BigDecimal,
    val photo: String
) {
    // DTO -> Entity 변환 메서드
    fun toEntity(): Location {
        return Location(
            placeId = placeId,
            placeName = placeName,
            latitude = latitude,
            longitude = longitude,
            address = address,
            rating = rating,
            photo = photo
        )
    }
}