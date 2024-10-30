package com.grepp.nbe1_3_team9.domain.repository.group.membership

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import com.grepp.nbe1_3_team9.domain.entity.group.QGroup
import com.grepp.nbe1_3_team9.domain.entity.group.QGroupMembership.groupMembership
import com.grepp.nbe1_3_team9.domain.entity.user.QUser
import com.grepp.nbe1_3_team9.domain.entity.user.QUser.user
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.util.*

class GroupMembershipRepositoryImpl(em: EntityManager) : GroupMembershipRepositoryCustom {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)



    override fun findByGroupAndUser(group: Group, user: User): Optional<GroupMembership> {
        val qUser = QUser.user

        val result = queryFactory
            .selectFrom(groupMembership)
            .leftJoin(groupMembership.user, qUser).fetchJoin()
            .where(groupMembership.group.eq(group)
                .and(groupMembership.user.eq(user)))
            .fetchOne()

        return Optional.ofNullable(result)
    }

    override fun findByUserId(userId: Long): List<GroupMembership> {
        return queryFactory
            .selectFrom(groupMembership)
            .leftJoin(groupMembership.user, user).fetchJoin()
            .where(groupMembership.user.userId.eq(userId))
            .fetch()
    }

    override fun findByGroup(group: Group): List<GroupMembership> {
        val qGroup = QGroup.group

        return queryFactory
            .selectFrom(groupMembership)
            .leftJoin(groupMembership.group, qGroup).fetchJoin()
            .where(groupMembership.group.eq(group))
            .fetch()
    }

}
