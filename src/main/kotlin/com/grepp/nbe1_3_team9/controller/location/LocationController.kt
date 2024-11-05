package com.grepp.nbe1_3_team9.controller.location

import com.grepp.nbe1_3_team9.controller.location.dto.CreateLocationRequest
import com.grepp.nbe1_3_team9.controller.location.dto.LocationDto
import com.grepp.nbe1_3_team9.domain.service.location.LocationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations")
class LocationController(private val locationService: LocationService) {

    // 장소 저장
    @PostMapping
    fun createLocation(@RequestBody locationReq: CreateLocationRequest): ResponseEntity<LocationDto> {
        val createdLocation = locationService.saveLocation(locationReq)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation)
    }

    // 장소 조회
    @GetMapping("/{locationId}")
    fun getLocation(@PathVariable locationId: Long): ResponseEntity<LocationDto> {
        val location = locationService.getLocationById(locationId)
        return ResponseEntity.ok(location)
    }

    // 장소 삭제
    @DeleteMapping("/{locationId}")
    fun deleteLocation(@PathVariable locationId: Long): ResponseEntity<Void> {
        locationService.deleteLocationById(locationId)
        return ResponseEntity.noContent().build()
    }
}
