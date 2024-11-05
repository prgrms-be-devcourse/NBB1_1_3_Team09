package com.grepp.nbe1_3_team9.domain.service.event

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.EventException
import com.grepp.nbe1_3_team9.common.exception.exceptions.EventLocationException
import com.grepp.nbe1_3_team9.common.exception.exceptions.LocationException
import com.grepp.nbe1_3_team9.controller.event.dto.AddEventLocationReq
import com.grepp.nbe1_3_team9.controller.event.dto.EventLocationDto
import com.grepp.nbe1_3_team9.controller.event.dto.EventLocationInfoDto
import com.grepp.nbe1_3_team9.controller.event.dto.UpdateEventLocationReq
import com.grepp.nbe1_3_team9.domain.entity.Location
import com.grepp.nbe1_3_team9.domain.entity.event.Event
import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import com.grepp.nbe1_3_team9.domain.repository.event.eventLocRepo.EventLocationRepository
import com.grepp.nbe1_3_team9.domain.repository.event.eventrepo.EventRepository
import com.grepp.nbe1_3_team9.domain.repository.location.LocationRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class EventLocationService(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository,
    private val eventLocationRepository: EventLocationRepository,
    private val eventLocationRedisTemplate: RedisTemplate<String, String>

) {
    private val log = LoggerFactory.getLogger(EventLocationService::class.java)

    // 일정에 장소 추가
    @Transactional
    fun addLocationToEvent(eventId: Long, req: AddEventLocationReq): EventLocationDto {
        val event = findEventByIdOrThrowEventException(eventId)
        val location = findLocationByIdOrThrowLocationException(req.locationId)

        // 저장할 때 겹치는 시간대 체크
        if (eventLocationRepository.existsByEventIdAndVisitTimes(eventId, req.visitStartTime, req.visitEndTime)) {
            throw EventLocationException(ExceptionMessage.UNAVAILABLE_TIME)
        }

        val eventLocation = EventLocation.create(
            event = event,
            location = location,
            description = req.description,
            visitStartTime = req.visitStartTime,
            visitEndTime = req.visitEndTime,
            color = req.color
        )

        val savedEventLocation = eventLocationRepository.save(eventLocation)
        return EventLocationDto.from(savedEventLocation)
    }

    // 일정에 포함된 장소 불러오기
    fun getEventLocations(eventId: Long): List<EventLocationInfoDto> {
        val event = findEventByIdOrThrowEventException(eventId)
        return eventLocationRepository.findByEvent(event).map { EventLocationInfoDto.from(it) }
    }

    // 일정에 포함되고 선택한 날짜와 같은 장소 불러오기 (시간 빠른 순서)
    fun getEventLocationByDate(eventId: Long, date: LocalDate): List<EventLocationInfoDto> {
        val event = findEventByIdOrThrowEventException(eventId)
        return eventLocationRepository.findByEventAndDate(event, date).map { EventLocationInfoDto.from(it) }
    }

    // 장소아이디로 일정 가져오기
    @Transactional
    fun getEventLocationsById(pinId: Long): EventLocationInfoDto {
        val lockKey = "lock:eventLocation:$pinId"

        // Redis에서 락을 확인 후 설정
        if (eventLocationRedisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofMinutes(1)) == false) {
            throw EventException(ExceptionMessage.EVENT_LOCATION_LOCKED)
        }
        val eventLocation = findEventLocationByIdOrThrowException(pinId)
        return EventLocationInfoDto.from(eventLocation)
    }

    @Transactional
    fun updateEventLocation(pinId: Long, req: UpdateEventLocationReq): EventLocationDto {
        val eventLocation = findEventLocationByIdOrThrowException(pinId)

        req.description?.let { eventLocation.updateDescription(it) }
        if (req.visitStartTime != null && req.visitEndTime != null) {
            eventLocation.updateVisitTime(req.visitStartTime, req.visitEndTime)
        }
        // 수정 완료 후 락 해제
        unlockLocation(pinId)

        return EventLocationDto.from(eventLocation)
    }

    // 일정에 포함된 장소 삭제
    @Transactional
    fun removeLocationFromEvent(pinId: Long) {
        val eventLocation = findEventLocationByIdOrThrowException(pinId)
        eventLocationRepository.delete(eventLocation)
    }

    //락 해제
    fun unlockLocation(pinId: Long) {
        val lockKey = "lock:eventLocation:$pinId"
        eventLocationRedisTemplate.delete(lockKey)
    }


    // 예외 처리
    private fun findEventByIdOrThrowEventException(eventId: Long): Event {
        return eventRepository.findById(eventId).orElseThrow {
            log.warn(">>>> EventId {} : {} <<<<", eventId, ExceptionMessage.EVENT_NOT_FOUND)
            EventException(ExceptionMessage.EVENT_NOT_FOUND)
        }
    }

    private fun findLocationByIdOrThrowLocationException(locationId: Long): Location {
        return locationRepository.findById(locationId).orElseThrow {
            log.warn(">>>> LocationId {} : {} <<<<", locationId, ExceptionMessage.LOCATION_NOT_FOUND)
            LocationException(ExceptionMessage.LOCATION_NOT_FOUND)
        }
    }

    private fun findEventLocationByIdOrThrowException(pinId: Long): EventLocation {
        return eventLocationRepository.findById(pinId).orElseThrow {
            log.warn(">>>> PinId {} : {} <<<<", pinId, ExceptionMessage.LOCATION_NOT_FOUND)
            LocationException(ExceptionMessage.LOCATION_NOT_FOUND)
        }
    }
}
