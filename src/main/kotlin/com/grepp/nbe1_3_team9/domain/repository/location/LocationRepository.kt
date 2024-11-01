package com.grepp.nbe1_3_team9.domain.repository.location

import com.grepp.nbe1_3_team9.domain.entity.Location
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface LocationRepository : JpaRepository<Location, Long> {
    fun findByPlaceId(placeId: String): Optional<Location>
}