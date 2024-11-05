package com.grepp.nbe1_3_team9.controller.location.dto

import com.grepp.nbe1_3_team9.domain.entity.Location
import java.math.BigDecimal

data class LocationDto(
    val locationId: Long?,
    val placeId: String,
    val placeName: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val address: String,
    val rating: BigDecimal?,
    val photo: String?
) {
    companion object {
        fun fromEntity(location: Location): LocationDto {
            return LocationDto(
                locationId = location.locationId,
                placeId = location.placeId,
                placeName = location.placeName,
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address,
                rating = location.rating,
                photo = location.photo
            )
        }
    }
}