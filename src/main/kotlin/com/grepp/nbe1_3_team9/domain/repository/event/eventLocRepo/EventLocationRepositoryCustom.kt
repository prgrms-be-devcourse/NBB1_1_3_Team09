package com.grepp.nbe1_3_team9.domain.repository.event.eventLocRepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import java.time.LocalDate
import java.time.LocalDateTime

interface EventLocationRepositoryCustom {
    fun findByEventAndDate(event: Event, date: LocalDate): List<EventLocation>

    fun existsByEventIdAndVisitTimes(eventId: Long, visitStartTime: LocalDateTime, visitEndTime: LocalDateTime): Boolean

}
