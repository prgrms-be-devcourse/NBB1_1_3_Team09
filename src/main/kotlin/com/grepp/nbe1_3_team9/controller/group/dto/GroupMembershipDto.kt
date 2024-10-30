package com.grepp.nbe1_3_team9.controller.group.dto

import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import com.grepp.nbe1_3_team9.domain.entity.group.GroupRole

data class GroupMembershipDto(
    val id: Long,
    val groupId: Long,
    val username: String,
    val role: GroupRole
) {
    companion object {
        fun from(membership: GroupMembership): GroupMembershipDto {
            return GroupMembershipDto(
                id = membership.membershipId,
                groupId = membership.group.groupId,
                username = membership.user.username,
                role = membership.role
            )
        }
    }
}