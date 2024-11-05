package com.grepp.nbe1_3_team9.controller.event.dto

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class UpdateEventLocationReq(
    @field:Size(max = 500, message = "설명은 초대 500자")
    val description: String? = null,

    @field:NotNull(message = "방문 시작 시간 필수")
    val visitStartTime: LocalDateTime,

    @field:NotNull(message = "방문 종료 시간 필수")
    val visitEndTime: LocalDateTime
) {
    @AssertTrue(message = "방문 종료 시간은 시작 시간 이후여야 합니다.")
    fun isValidTimeRange(): Boolean {
        return (visitStartTime == null && visitEndTime == null) ||
                (visitStartTime != null && visitEndTime != null && !visitEndTime.isBefore(visitStartTime))
    }
}
