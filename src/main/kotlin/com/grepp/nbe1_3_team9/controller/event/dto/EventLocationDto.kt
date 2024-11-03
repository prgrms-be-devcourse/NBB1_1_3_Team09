package com.grepp.nbe1_3_team9.controller.event.dto


import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import java.time.LocalDateTime

data class EventLocationDto(
    val pinId: Long,
    val eventId: Long,
    val locationId: Long,
    val description: String?,
    val visitStart: LocalDateTime,
    val visitEnd: LocalDateTime
) {
    companion object {
        fun from(eventLocation: EventLocation): EventLocationDto {
            return EventLocationDto(
                pinId = eventLocation.pinId,
                eventId = eventLocation.event.eventId,
                locationId = eventLocation.location.locationId,
                description = eventLocation.description,
                visitStart = eventLocation.visitStartTime,
                visitEnd = eventLocation.visitEndTime
            )
        }
    }
}