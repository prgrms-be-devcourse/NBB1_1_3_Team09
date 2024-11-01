package com.grepp.nbe1_3_team9.domain.service.event

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.EventException
import com.grepp.nbe1_3_team9.common.exception.exceptions.GroupException
import com.grepp.nbe1_3_team9.controller.event.dto.CreateEventRequest
import com.grepp.nbe1_3_team9.controller.event.dto.EventDto
import com.grepp.nbe1_3_team9.controller.event.dto.UpdateEventRequest
import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.repository.event.eventrepo.EventRepository
import com.grepp.nbe1_3_team9.domain.repository.group.GroupRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class EventService(
    private val eventRepository: EventRepository,
    private val groupRepository: GroupRepository
) {
    private val log = LoggerFactory.getLogger(EventService::class.java)

    @Transactional
    fun createEvent(request: CreateEventRequest): EventDto {
        val group = findGroupByIdOrThrowGroupException(request.groupId)
        val event = Event(
            eventName = request.eventName,
            description = request.description,
            city = request.city,
            startDate = request.startDate,
            endDate = request.endDate,
            group = group
        )
        val savedEvent = eventRepository.save(event)
        return EventDto.from(savedEvent)
    }

    fun getEventById(id: Long): EventDto {
        val event = findByIdOrThrowEventException(id)
        return EventDto.from(event)
    }

    @Transactional
    fun updateEvent(eventId: Long, request: UpdateEventRequest): EventDto {
        val event = findByIdOrThrowEventException(eventId)
        event.updateEventDetails(
            request.eventName,
            request.description,
            request.startDate,
            request.endDate
        )
        return EventDto.from(event)
    }

    @Transactional
    fun deleteEvent(eventId: Long) {
        if (!eventRepository.existsById(eventId)) {
            throw EventException(ExceptionMessage.EVENT_NOT_FOUND)
        }
        eventRepository.deleteById(eventId)
    }

    fun getEventsByGroup(groupId: Long): List<EventDto> {
        val group = findGroupByIdOrThrowGroupException(groupId)
        return eventRepository.findByGroup(group).map { EventDto.from(it) }
    }

    private fun findByIdOrThrowEventException(eventId: Long): Event {
        return eventRepository.findById(eventId).orElseThrow {
            log.warn(">>>> EventId {} : {} <<<<", eventId, ExceptionMessage.EVENT_NOT_FOUND)
            EventException(ExceptionMessage.EVENT_NOT_FOUND)
        }
    }

    private fun findGroupByIdOrThrowGroupException(groupId: Long): Group {
        return groupRepository.findById(groupId).orElseThrow {
            log.warn(">>>> GroupId {} : {} <<<<", groupId, ExceptionMessage.GROUP_NOT_FOUND)
            GroupException(ExceptionMessage.GROUP_NOT_FOUND)
        }
    }
}