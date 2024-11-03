package com.grepp.nbe1_3_team9.domain.repository.event.eventLocRepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import org.springframework.data.jpa.repository.JpaRepository

interface EventLocationRepository : JpaRepository<EventLocation, Long>, EventLocationRepositoryCustom {
    fun findByEvent(event: Event): List<EventLocation>
}
