package com.grepp.nbe1_3_team9.controller.group.dto

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.GroupStatus
import java.time.LocalDateTime

data class GroupDto(
    val groupId: Long,
    val groupName: String,
    val creationDate: LocalDateTime
) {
    companion object {
        fun from(group: Group): GroupDto {
            return GroupDto(
                groupId = group.groupId,
                groupName = group.groupName,
                creationDate = group.creationDate
            )
        }
    }
}
