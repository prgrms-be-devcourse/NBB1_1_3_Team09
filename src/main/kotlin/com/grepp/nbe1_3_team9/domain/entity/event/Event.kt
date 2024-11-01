package com.grepp.nbe1_3_team9.domain.entity.event

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "event_tb")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val eventId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    var group: Group,

    @Column(nullable = false, length = 100)
    var eventName: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var city: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EventStatus = EventStatus.UPCOMING,

    @Column(nullable = false)
    var startDate: LocalDate,

    @Column(nullable = false)
    var endDate: LocalDate,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], orphanRemoval = true)
    var eventLocations: MutableList<EventLocation> = mutableListOf()
) {

    // 비즈니스 메서드
    fun updateEventDetails(eventName: String, description: String?, startDate: LocalDate, endDate: LocalDate) {
        this.eventName = eventName
        this.description = description
        this.startDate = startDate
        this.endDate = endDate
    }

    fun updateStatus(status: EventStatus) {
        this.status = status
    }
}