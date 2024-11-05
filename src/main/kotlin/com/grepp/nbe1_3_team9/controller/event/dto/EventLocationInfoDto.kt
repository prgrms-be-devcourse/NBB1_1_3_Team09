package com.grepp.nbe1_3_team9.controller.event.dto

import com.grepp.nbe1_3_team9.controller.location.dto.LocationEventDto
import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import java.time.LocalDateTime

data class EventLocationInfoDto(
    val pinId: Long,
    val location: LocationEventDto,
    val description: String?,
    val visitStartTime: LocalDateTime,
    val visitEndTime: LocalDateTime,
    val color: String
) {
    companion object {
        fun from(eventLocation: EventLocation): EventLocationInfoDto {
            return EventLocationInfoDto(
                pinId = eventLocation.pinId,
                location = LocationEventDto.from(eventLocation.location),
                description = eventLocation.description,
                visitStartTime = eventLocation.visitStartTime,
                visitEndTime = eventLocation.visitEndTime,
                color = eventLocation.color
            )
        }
    }
}