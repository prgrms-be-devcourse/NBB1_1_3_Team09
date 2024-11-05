package com.grepp.nbe1_3_team9.controller.event.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class AddEventLocationReq(
    @field:NotNull(message = "위치 ID 필수")
    val locationId: Long,

    @field:Size(max = 500, message = "설명은 초대 500자")
    val description: String? = null,

    @field:NotNull(message = "방문 시작 시간 필수")
    val visitStartTime: LocalDateTime,

    @field:NotNull(message = "방문 종료 시간 필수")
    val visitEndTime: LocalDateTime,

    val color : String
) {
    @AssertTrue(message = "방문 종료 시간은 시작 시간 이후여야 합니다.")
    fun isValidTimeRange(): Boolean {
        return visitStartTime != null && visitEndTime != null && !visitEndTime.isBefore(visitStartTime)
    }
}
