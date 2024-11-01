package com.grepp.nbe1_3_team9.domain.entity.event

import com.grepp.nbe1_3_team9.domain.entity.Location
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "event_location_tb")
class EventLocation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val pinId: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    var event: Event,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    var location: Location,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var visitStartTime: LocalDateTime,

    @Column(nullable = false)
    var visitEndTime: LocalDateTime
) {
    // 비즈니스 메서드
    fun updateDescription(description: String?) {
        this.description = description
    }

    fun changeLocation(location: Location) {
        this.location = location
    }

    fun assignToEvent(event: Event) {
        this.event = event
    }

    fun updateVisitTime(visitStartTime: LocalDateTime, visitEndTime: LocalDateTime) {
        this.visitStartTime = visitStartTime
        this.visitEndTime = visitEndTime
    }

    companion object {
        fun create(
            event: Event,
            location: Location,
            description: String?,
            visitStartTime: LocalDateTime,
            visitEndTime: LocalDateTime
        ): EventLocation {
            return EventLocation(
                event = event,
                location = location,
                description = description,
                visitStartTime = visitStartTime,
                visitEndTime = visitEndTime
            )
        }
    }
}
