package com.grepp.nbe1_3_team9.domain.repository.event.eventrepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.event.QEvent
import com.grepp.nbe1_3_team9.domain.entity.event.QEvent.event
import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.QGroup
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager

class EventRepositoryImpl(
    entityManager: EntityManager
)  : EventRepositoryCustom{

    private val queryFactory : JPAQueryFactory = JPAQueryFactory(entityManager)
    override fun findByGroup(group: Group): List<Event> {
        val qGroup = QGroup.group
        return queryFactory
            .selectFrom(event)
            .leftJoin(event.group, qGroup).fetchJoin()
            .where(event.group.eq(group))
            .fetch()

    }

}
