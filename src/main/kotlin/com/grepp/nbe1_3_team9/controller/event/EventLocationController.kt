package com.grepp.nbe1_3_team9.controller.event

import com.grepp.nbe1_3_team9.controller.event.dto.*
import com.grepp.nbe1_3_team9.domain.service.event.EventLocationService
import jakarta.validation.Valid
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/events")
class EventLocationController(
    private val eventLocationService: EventLocationService,
    private val eventLocationRedisTemplate: RedisTemplate<String, String>

) {

    @PostMapping("/{eventId}/locations")
    fun addLocationToEvent(
        @PathVariable eventId: Long,
        @Valid @RequestBody request: AddEventLocationReq
    ): ResponseEntity<EventLocationDto> {
        val result = eventLocationService.addLocationToEvent(eventId, request)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @GetMapping("/{eventId}/locations")
    fun getEventLocations(@PathVariable eventId: Long): ResponseEntity<List<EventLocationInfoDto>> {
        val locations = eventLocationService.getEventLocations(eventId)
        return ResponseEntity.ok(locations)
    }

    @GetMapping("/{pinId}/eventLocations")
    fun getEventLocationsById(@PathVariable pinId: Long): ResponseEntity<EventLocationInfoDto> {
        val location = eventLocationService.getEventLocationsById(pinId)
        return ResponseEntity.ok(location)
    }

    @GetMapping("/{eventId}/locationsByDate")
    fun getEventLocationsByDate(
        @PathVariable eventId: Long,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<EventLocationInfoDto>> {
        val locations = eventLocationService.getEventLocationByDate(eventId, date)
        return ResponseEntity.ok(locations)
    }

    @PatchMapping("/{pinId}/eventLocations")
    fun updateEventLocation(
        @PathVariable pinId: Long,
        @Valid @RequestBody request: UpdateEventLocationReq
    ): ResponseEntity<EventLocationDto> {
        val result = eventLocationService.updateEventLocation(pinId, request)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{pinId}/eventLocations")
    fun removeLocationFromEvent(@PathVariable pinId: Long): ResponseEntity<Void> {
        eventLocationService.removeLocationFromEvent(pinId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/unlockLocation")
    fun unlockLocation(@RequestBody request: UnlockLocationRequest): ResponseEntity<Void> {
        val pinId = request.pinId
        val lockKey = "lock:eventLocation:$pinId"
        eventLocationRedisTemplate.delete(lockKey) // 락 해제
        return ResponseEntity.ok().build()
    }
}
