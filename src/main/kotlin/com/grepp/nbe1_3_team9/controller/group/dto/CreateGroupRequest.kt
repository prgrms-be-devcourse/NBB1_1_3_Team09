package com.grepp.nbe1_3_team9.controller.group.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateGroupRequest(
    @field:NotBlank(message = "그룹 이름 입력 필수")
    @field:Size(min = 2, max = 50, message = "그룹 이름은 2글자 이상 50글자 미만으로 해야합니다")
    val groupName: String
)
