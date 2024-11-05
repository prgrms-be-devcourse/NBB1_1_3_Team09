package com.grepp.nbe1_3_team9.domain.entity

import com.grepp.nbe1_3_team9.domain.entity.event.EventLocation
import jakarta.persistence.*
import java.io.Serializable
import java.math.BigDecimal

@Entity
@Table(name = "location_tb")
data class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val locationId: Long = 0L,

    @Column(unique = true)
    val placeId: String,

    @Column(nullable = false, length = 100)
    val placeName: String,

    @Column(nullable = false, precision = 10, scale = 8)
    var latitude: BigDecimal,

    @Column(nullable = false, precision = 11, scale = 8)
    var longitude: BigDecimal,

    var address: String,

    @Column(precision = 3, scale = 2)
    var rating: BigDecimal? = null,

    @Column(columnDefinition = "TEXT")
    val photo: String? = null,

    @OneToMany(mappedBy = "location", cascade = [CascadeType.ALL], orphanRemoval = true)
    val eventLocations: List<EventLocation> = mutableListOf(),

    ) : Serializable {

    // 비즈니스 메서드
    fun updateLocationDetails(address: String, rating: BigDecimal) {
        this.address = address
        this.rating = rating
    }

    fun updateCoordinates(latitude: BigDecimal, longitude: BigDecimal) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateRating(rating: BigDecimal) {
        this.rating = rating
    }
}
