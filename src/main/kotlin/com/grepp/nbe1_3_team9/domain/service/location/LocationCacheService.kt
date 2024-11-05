package com.grepp.nbe1_3_team9.domain.service.location

import com.grepp.nbe1_3_team9.domain.entity.Location
import com.grepp.nbe1_3_team9.domain.repository.location.LocationRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class LocationCacheService(
    private val locationRepository: LocationRepository
) {

    @Cacheable(value = ["locationCache"], key = "#placeId")
    fun findLocationByPlaceId(placeId: String): Optional<Location> {
        return locationRepository.findByPlaceId(placeId)
    }

    @CachePut(value = ["locationCache"], key = "#location.placeId")
    fun saveLocationToCache(location: Location): Location {
        return location
    }
}
