package com.grepp.nbe1_3_team9.domain.repository.group.membership

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import com.grepp.nbe1_3_team9.domain.entity.user.User
import java.util.*

interface GroupMembershipRepositoryCustom {
    fun findByGroupAndUser(group: Group, user: User): Optional<GroupMembership>
    fun findByUserId(userId: Long): List<GroupMembership>
    fun findByGroup(group: Group): List<GroupMembership>
}
