package com.grepp.nbe1_3_team9.domain.entity.group

import com.grepp.nbe1_3_team9.domain.entity.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "group_membership_tb")
class GroupMembership private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val membershipId: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val joinDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: GroupRole = GroupRole.MEMBER
) {
    // 비즈니스 메서드

    // 그룹 탈퇴 같은 상황에서 Role 변경 로직
    fun changeRole(newRole: GroupRole) {
        this.role = newRole
    }

    override fun toString(): String {
        return "GroupMembership(membershipId=$membershipId, joinDate=$joinDate, role=$role)"
    }

    companion object {
        fun create(group: Group, user: User, role: GroupRole = GroupRole.MEMBER) = GroupMembership(
            group = group,
            user = user,
            role = role
        )
    }
}
