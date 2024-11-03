package com.grepp.nbe1_3_team9.controller.event

import com.grepp.nbe1_3_team9.controller.event.dto.CreateEventRequest
import com.grepp.nbe1_3_team9.controller.event.dto.EventDto
import com.grepp.nbe1_3_team9.controller.event.dto.UpdateEventRequest
import com.grepp.nbe1_3_team9.domain.service.event.EventService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/events")
class EventController(
    private val eventService: EventService
) {

    @PostMapping
    fun createEvent(@Valid @RequestBody request: CreateEventRequest): ResponseEntity<EventDto> {
        val eventDto = eventService.createEvent(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto)
    }

    @GetMapping("/{eventId}")
    fun getEvent(@PathVariable eventId: Long): ResponseEntity<EventDto> {
        val eventDto = eventService.getEventById(eventId)
        return ResponseEntity.ok(eventDto)
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: Long,
        @Valid @RequestBody request: UpdateEventRequest
    ): ResponseEntity<EventDto> {
        val eventDto = eventService.updateEvent(eventId, request)
        return ResponseEntity.ok(eventDto)
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(@PathVariable eventId: Long): ResponseEntity<Void> {
        eventService.deleteEvent(eventId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/group/{groupId}")
    fun getGroupEvents(@PathVariable groupId: Long): ResponseEntity<List<EventDto>> {
        val eventDtos = eventService.getEventsByGroup(groupId)
        return ResponseEntity.ok(eventDtos)
    }
}