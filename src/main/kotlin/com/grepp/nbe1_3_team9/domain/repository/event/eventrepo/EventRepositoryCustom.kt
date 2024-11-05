package com.grepp.nbe1_3_team9.domain.repository.event.eventrepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.group.Group

interface EventRepositoryCustom {
    fun findByGroup(group: Group): List<Event>
}
