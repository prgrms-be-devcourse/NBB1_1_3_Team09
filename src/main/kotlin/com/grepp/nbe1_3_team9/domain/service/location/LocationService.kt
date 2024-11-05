package com.grepp.nbe1_3_team9.domain.service.location

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.LocationException
import com.grepp.nbe1_3_team9.controller.location.dto.CreateLocationRequest
import com.grepp.nbe1_3_team9.controller.location.dto.LocationDto
import com.grepp.nbe1_3_team9.domain.repository.location.LocationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LocationService(
    private val locationRepository: LocationRepository,
    private val locationCacheService: LocationCacheService
) {

    private val logger = LoggerFactory.getLogger(LocationService::class.java)

    //@LogExecutionTime
    @Transactional
    fun saveLocation(locationReq: CreateLocationRequest): LocationDto {
        // 캐시 사용
        val existingLocation = locationCacheService.findLocationByPlaceId(locationReq.placeId)

        if (existingLocation.isPresent) {
            return LocationDto.fromEntity(existingLocation.get())
        }
        //db에 있는지 확인
        val locationFromDb = locationRepository.findByPlaceId(locationReq.placeId)

        if (locationFromDb.isPresent) {
            return LocationDto.fromEntity(locationFromDb.get())
        }
        val savedLocation = locationRepository.save(locationReq.toEntity())
        // 저장 후 캐시에 추가
        locationCacheService.saveLocationToCache(savedLocation)
        return LocationDto.fromEntity(savedLocation)
    }

    // 장소 조회
    @Transactional
    fun getLocationById(locationId: Long): LocationDto {
        val location = locationRepository.findById(locationId)
            .orElseThrow { LocationException(ExceptionMessage.LOCATION_NOT_FOUND) }
        return LocationDto.fromEntity(location)
    }

    // 장소 삭제
    @Transactional
    fun deleteLocationById(locationId: Long) {
        locationRepository.deleteById(locationId)
    }
}