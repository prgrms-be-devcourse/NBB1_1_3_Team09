package com.grepp.nbe1_3_team9.controller.event.dto

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import java.time.LocalDate

data class EventDto(
    val id: Long,
    val eventName: String,
    val description: String?,
    val city: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupId: Long
) {
    companion object {
        fun from(event: Event): EventDto {
            return EventDto(
                id = event.eventId,
                eventName = event.eventName,
                description = event.description,
                city = event.city,
                startDate = event.startDate,
                endDate = event.endDate,
                groupId = event.group.groupId
            )
        }
    }
}
