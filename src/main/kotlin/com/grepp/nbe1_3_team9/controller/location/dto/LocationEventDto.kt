package com.grepp.nbe1_3_team9.controller.location.dto

import com.grepp.nbe1_3_team9.domain.entity.Location
import java.math.BigDecimal

data class LocationEventDto(
    val locationId: Long?,
    val placeName: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val address: String?,
    val rating: BigDecimal?
) {
    companion object {
        fun from(location: Location): LocationEventDto {
            return LocationEventDto(
                locationId = location.locationId,
                placeName = location.placeName,
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address,
                rating = location.rating
            )
        }
    }
}
