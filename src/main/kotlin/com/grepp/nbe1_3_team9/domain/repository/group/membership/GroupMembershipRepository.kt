package com.grepp.nbe1_3_team9.domain.repository.group.membership

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import com.grepp.nbe1_3_team9.domain.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface GroupMembershipRepository : JpaRepository<GroupMembership, Long>, GroupMembershipRepositoryCustom {
    fun existsByGroupAndUser(group: Group, user: User): Boolean
}
