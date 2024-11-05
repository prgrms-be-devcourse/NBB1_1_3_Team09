package com.grepp.nbe1_3_team9.domain.repository.event.eventLocRepo

import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import com.grepp.nbe1_3_team9.domain.entity.event.QEventLocation
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class EventLocationRepositoryImpl(
    private val entityManager: EntityManager
) : EventLocationRepositoryCustom {

    private val queryFactory = JPAQueryFactory(entityManager)

    override fun findByEventAndDate(event: Event, date: LocalDate): List<EventLocation> {
        val eventLocation = QEventLocation.eventLocation
        val formattedDate = Expressions.stringTemplate(
            "DATE_FORMAT({0}, {1})", eventLocation.visitStartTime, "%Y-%m-%d"
        )
        return queryFactory.selectFrom(eventLocation)
            .where(eventLocation.event.eq(event).and(formattedDate.eq(date.toString())))
            .orderBy(eventLocation.visitStartTime.asc())
            .fetch()
    }

    override fun existsByEventIdAndVisitTimes(eventId: Long, visitStartTime: LocalDateTime, visitEndTime: LocalDateTime): Boolean {
        val eventLocation = QEventLocation.eventLocation

        return queryFactory.selectFrom(eventLocation)
            .where(
                eventLocation.event.eventId.eq(eventId)
                    .and(eventLocation.visitStartTime.lt(visitEndTime))
                    .and(eventLocation.visitEndTime.gt(visitStartTime))
            )
            .fetchOne() != null
    }
}
