package com.grepp.nbe1_3_team9.domain.service.group

import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.GroupException
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.controller.group.dto.CreateGroupRequest
import com.grepp.nbe1_3_team9.controller.group.dto.GroupDto
import com.grepp.nbe1_3_team9.controller.group.dto.GroupMembershipDto
import com.grepp.nbe1_3_team9.controller.group.dto.UpdateGroupRequest
import com.grepp.nbe1_3_team9.domain.entity.group.Group
import com.grepp.nbe1_3_team9.domain.entity.group.GroupMembership
import com.grepp.nbe1_3_team9.domain.entity.group.GroupRole
import com.grepp.nbe1_3_team9.domain.entity.group.invitaion.GroupInvitation
import com.grepp.nbe1_3_team9.domain.entity.user.User
import com.grepp.nbe1_3_team9.domain.repository.group.GroupInvitationRepository
import com.grepp.nbe1_3_team9.domain.repository.group.GroupRepository
import com.grepp.nbe1_3_team9.domain.repository.group.membership.GroupMembershipRepository
import com.grepp.nbe1_3_team9.domain.repository.user.UserRepository
import com.grepp.nbe1_3_team9.notification.controller.dto.NotificationResp
import com.grepp.nbe1_3_team9.notification.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class GroupService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val groupMembershipRepository: GroupMembershipRepository,
    private val groupInvitationRepository: GroupInvitationRepository,
    private val notificationService: NotificationService
) {
    private val log = LoggerFactory.getLogger(GroupService::class.java)

    @Transactional
    fun createGroup(request: CreateGroupRequest, creatorId: Long): GroupDto {
        val creator = findUserByIdOrThrowUserException(creatorId)

        val group = Group.create(request.groupName)
        val savedGroup = groupRepository.save(group)

        val membership = GroupMembership.create(savedGroup, creator, GroupRole.OWNER)
        groupMembershipRepository.save(membership)

        return GroupDto.from(savedGroup)
    }

    fun getGroupById(id: Long): GroupDto {
        val group = findGroupByIdOrThrowGroupException(id)
        return GroupDto.from(group)
    }

    fun getUserGroups(userId: Long): List<GroupDto> {
        return groupMembershipRepository.findByUserId(userId)
            .map { it.group }
            .map { GroupDto.from(it) }
    }

    @Transactional
    fun updateGroup(id: Long, request: UpdateGroupRequest, userId: Long): GroupDto {
        val user = findUserByIdOrThrowUserException(userId)
        val group = findGroupByIdOrThrowGroupException(id)

        val groupMembership = findGroupMemberByGroupAndUserThrowGroupException(group, user)

        if (groupMembership.role != GroupRole.OWNER) {
            throw GroupException(ExceptionMessage.GROUP_OWNER_ACCESS_ONLY)
        }

        group.updateGroupName(request.groupName)
        val updatedGroup = groupRepository.save(group)
        return GroupDto.from(updatedGroup)
    }

    @Transactional
    fun deleteGroup(id: Long, userId: Long) {
        val user = findUserByIdOrThrowUserException(userId)
        val group = findGroupByIdOrThrowGroupException(id)

        val groupMembership = findGroupMemberByGroupAndUserThrowGroupException(group, user)

        if (groupMembership.role != GroupRole.OWNER) {
            throw GroupException(ExceptionMessage.GROUP_OWNER_ACCESS_ONLY)
        }

        groupMembershipRepository.deleteByGroup(group)

        groupRepository.delete(group)
    }

    @Transactional
    fun inviteMemberToGroup(groupId: Long, email: String, adminId: Long) {
        val group = findGroupByIdOrThrowGroupException(groupId)
        val inviter = findUserByIdOrThrowUserException(adminId)
        val invitee = findUserByEmailOrThrowUserException(email)

        val groupMembership = findGroupMemberByGroupAndUserThrowGroupException(group, inviter)
        if (groupMembership.role != GroupRole.ADMIN && groupMembership.role != GroupRole.OWNER) {
            throw GroupException(ExceptionMessage.GROUP_ADMIN_ACCESS_ONLY)
        }

        if (groupMembershipRepository.existsByGroupAndUser(group, invitee)) {
            throw GroupException(ExceptionMessage.USER_ALREADY_IN_GROUP)
        }

        val invitation = GroupInvitation.create(group, inviter, invitee)
        val savedInvitation = groupInvitationRepository.save(invitation)

        val responseResp = NotificationResp(
            UUID.randomUUID().toString(),
            "INVITE",
            "${group.groupName} 그룹에 초대되셨습니다.",
            inviter.userId,
            invitee.userId,
            savedInvitation.id,
            LocalDateTime.now().toString(),
            false
        )

        notificationService.sendNotificationAsync(responseResp)
    }

    @Transactional
    fun acceptInvitation(invitationId: Long, userId: Long) {
        val invitation = findInvitationByIdOrThrowException(invitationId)
        if (invitation.invitee.userId != userId) {
            throw GroupException(ExceptionMessage.NOT_INVITED_USER)
        }

        invitation.accept()
        groupInvitationRepository.save(invitation)

        val membership = GroupMembership.create(invitation.group, invitation.invitee, GroupRole.MEMBER)
        groupMembershipRepository.save(membership)

        val responseResp = NotificationResp(
            UUID.randomUUID().toString(),
            "ACCEPT",
            "${invitation.invitee.username}님이 그룹에 참여했습니다.",
            invitation.inviter.userId,
            invitation.invitee.userId,
            invitationId,
            LocalDateTime.now().toString(),
            false
        )

        notificationService.sendNotificationAsync(responseResp)
    }

    @Transactional
    fun rejectInvitation(invitationId: Long, userId: Long) {
        val invitation = findInvitationByIdOrThrowException(invitationId)
        if (invitation.invitee.userId != userId) {
            throw GroupException(ExceptionMessage.NOT_INVITED_USER)
        }

        invitation.reject()
        groupInvitationRepository.save(invitation)

        val responseResp = NotificationResp(
            UUID.randomUUID().toString(),
            "REJECT",
            "${invitation.invitee.username}님이 그룹 초대를 거절했습니다.",
            invitation.inviter.userId,
            invitation.invitee.userId,
            invitationId,
            LocalDateTime.now().toString(),
            false
        )

        notificationService.sendNotificationAsync(responseResp)
    }

    @Transactional
    fun changeGroupMemberRole(groupId: Long, targetUsername: String, role: GroupRole, userId: Long) {
        val group = findGroupByIdOrThrowGroupException(groupId)
        val targetUser = findUserByUsernameOrThrowUserException(targetUsername)
        val user = findUserByIdOrThrowUserException(userId)

        val targetUserMembership = findGroupMemberByGroupAndUserThrowGroupException(group, targetUser)
        val membership = findGroupMemberByGroupAndUserThrowGroupException(group, user)

        if (membership.role != GroupRole.OWNER) {
            throw GroupException(ExceptionMessage.INSUFFICIENT_PERMISSION)
        }

        if (membership.role.priority <= targetUserMembership.role.priority) {
            throw GroupException(ExceptionMessage.INSUFFICIENT_PERMISSION)
        }

        targetUserMembership.changeRole(role)
    }

    @Transactional
    fun removeMemberFromGroup(groupId: Long, userId: Long, targetUsername: String) {
        val group = findGroupByIdOrThrowGroupException(groupId)
        val user = findUserByIdOrThrowUserException(userId)
        val targetUser = findUserByUsernameOrThrowUserException(targetUsername)

        val membership = findGroupMemberByGroupAndUserThrowGroupException(group, user)
        val targetUserMembership = findGroupMemberByGroupAndUserThrowGroupException(group, targetUser)

        if (membership.role.priority <= targetUserMembership.role.priority) {
            throw GroupException(ExceptionMessage.INSUFFICIENT_PERMISSION)
        }

        groupMembershipRepository.delete(targetUserMembership)
    }

    fun getGroupMembers(groupId: Long): List<GroupMembershipDto> {
        val group = findGroupByIdOrThrowGroupException(groupId)
        val memberships = groupMembershipRepository.findByGroup(group)
        return memberships.map { GroupMembershipDto.from(it) }
    }

    private fun findUserByEmailOrThrowUserException(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow {
                log.warn(">>>> UserEmail {} : {} <<<<", email, ExceptionMessage.USER_NOT_FOUND)
                UserException(ExceptionMessage.USER_NOT_FOUND)
            }
    }

    private fun findUserByIdOrThrowUserException(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow {
                log.warn(">>>> UserId {} : {} <<<<", userId, ExceptionMessage.USER_NOT_FOUND)
                UserException(ExceptionMessage.USER_NOT_FOUND)
            }
    }

    private fun findUserByUsernameOrThrowUserException(username: String): User {
        return userRepository.findByUsername(username)
            .orElseThrow {
                log.warn(">>>> Username {} : {} <<<<", username, ExceptionMessage.USER_NOT_FOUND)
                UserException(ExceptionMessage.USER_NOT_FOUND)
            }
    }

    private fun findGroupByIdOrThrowGroupException(groupId: Long): Group {
        return groupRepository.findById(groupId)
            .orElseThrow {
                log.warn(">>>> GroupId {} : {} <<<<", groupId, ExceptionMessage.GROUP_NOT_FOUND)
                GroupException(ExceptionMessage.GROUP_NOT_FOUND)
            }
    }

    private fun findGroupMemberByGroupAndUserThrowGroupException(group: Group, user: User): GroupMembership {
        return groupMembershipRepository.findByGroupAndUser(group, user)
            .orElseThrow {
                log.warn(">>>> User {} not in Group {} : {}", user.userId, group.groupId, ExceptionMessage.USER_NOT_FOUND)
                GroupException(ExceptionMessage.USER_NOT_IN_GROUP)
            }
    }

    private fun findInvitationByIdOrThrowException(invitationId: Long): GroupInvitation {
        return groupInvitationRepository.findById(invitationId)
            .orElseThrow {
                log.warn(">>>> InvitationId {} : {} <<<<", invitationId, ExceptionMessage.INVITATION_NOT_FOUND)
                GroupException(ExceptionMessage.INVITATION_NOT_FOUND)
            }
    }
}
