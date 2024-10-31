package com.grepp.nbe1_3_team9.domain.entity.group

import com.grepp.nbe1_3_team9.domain.entity.group.invitaion.GroupInvitation
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "group_tb")
data class Group(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val groupId: Long = 0,

    @Column(nullable = false, length = 100)
    var groupName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var groupStatus: GroupStatus = GroupStatus.ACTIVE,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val creationDate: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberships: MutableList<GroupMembership> = mutableListOf(),

//    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val events: MutableList<Event> = mutableListOf(),
//
//    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val tasks: MutableList<Task> = mutableListOf(),
//
//    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val expenses: MutableList<Expense> = mutableListOf(),

    @OneToMany(mappedBy = "group", cascade = [CascadeType.ALL], orphanRemoval = true)
    val groupInvitations: MutableList<GroupInvitation> = mutableListOf()
) {
    // 기본 비즈니스 메서드

    // 그룹 이름 변경
    fun updateGroupName(newName: String) {
        groupName = newName
    }

    fun updateStatus(newStatus: GroupStatus) {
        groupStatus = newStatus
    }

    companion object {
        fun create(groupName: String) = Group(groupName = groupName)
    }
}
