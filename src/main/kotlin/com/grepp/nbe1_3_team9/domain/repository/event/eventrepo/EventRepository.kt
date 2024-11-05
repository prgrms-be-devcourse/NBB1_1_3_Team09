package com.grepp.nbe1_3_team9.domain.repository.event.eventrepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, Long>, EventRepositoryCustom{
    fun findByEventId(eventId: Long): Event
}
