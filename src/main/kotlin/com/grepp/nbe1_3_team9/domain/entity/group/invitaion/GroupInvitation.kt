package com.grepp.nbe1_3_team9.domain.entity.group.invitaion

import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.user.User
import jakarta.persistence.*

@Entity
class GroupInvitation private constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    val group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    val inviter: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", nullable = false)
    val invitee: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: InvitationStatus = InvitationStatus.PENDING
) {

    fun accept() {
        status = InvitationStatus.ACCEPTED
    }

    fun reject() {
        status = InvitationStatus.REJECTED
    }

    companion object {
        fun create(group: Group, inviter: User, invitee: User) =
            GroupInvitation(group = group, inviter = inviter, invitee = invitee)
    }
}
