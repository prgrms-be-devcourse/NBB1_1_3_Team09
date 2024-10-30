package com.grepp.nbe1_3_team9.controller.group

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.controller.group.dto.CreateGroupRequest
import com.grepp.nbe1_3_team9.controller.group.dto.GroupDto
import com.grepp.nbe1_3_team9.controller.group.dto.GroupMembershipDto
import com.grepp.nbe1_3_team9.controller.group.dto.UpdateGroupRequest
import com.grepp.nbe1_3_team9.domain.entity.group.GroupRole
import com.grepp.nbe1_3_team9.domain.service.group.GroupService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/groups")
class GroupController(private val groupService: GroupService) {

    @PostMapping
    fun createGroup(@Valid @RequestBody request: CreateGroupRequest, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GroupDto> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        val groupDto = groupService.createGroup(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDto)
    }

    @GetMapping("/{groupId}")
    fun getGroup(@PathVariable groupId: Long): ResponseEntity<GroupDto> {
        val groupDto = groupService.getGroupById(groupId)
        return ResponseEntity.ok(groupDto)
    }

    @PutMapping("/{groupId}")
    fun updateGroup(@PathVariable groupId: Long, @Valid @RequestBody request: UpdateGroupRequest, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<GroupDto> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        val groupDto = groupService.updateGroup(groupId, request, userId)
        return ResponseEntity.ok(groupDto)
    }

    @DeleteMapping("/{groupId}")
    fun deleteGroup(@PathVariable groupId: Long, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.deleteGroup(groupId, userId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/user")
    fun getUserGroups(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<List<GroupDto>> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        val groups = groupService.getUserGroups(userId)
        return ResponseEntity.ok(groups)
    }

    @PostMapping("/{groupId}/members")
    fun addMemberToGroup(@PathVariable groupId: Long, @RequestParam email: String, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val adminId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.inviteMemberToGroup(groupId, email, adminId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/invitations/{invitationId}/accept")
    fun acceptInvitation(@PathVariable invitationId: Long, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.acceptInvitation(invitationId, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/invitations/{invitationId}/reject")
    fun rejectInvitation(@PathVariable invitationId: Long, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.rejectInvitation(invitationId, userId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{groupId}/members/{targetUsername}")
    fun removeMemberFromGroup(@PathVariable groupId: Long, @PathVariable targetUsername: String, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.removeMemberFromGroup(groupId, userId, targetUsername)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{groupId}/members")
    fun getGroupMembers(@PathVariable groupId: Long): ResponseEntity<List<GroupMembershipDto>> {
        val groupMembers = groupService.getGroupMembers(groupId)
        return ResponseEntity.ok(groupMembers)
    }

    @PutMapping("/{groupId}/members/{targetUsername}/role")
    fun changeGroupMemberRole(@PathVariable groupId: Long, @PathVariable targetUsername: String, @RequestParam role: GroupRole, @AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Void> {
        val userId = userDetails.user.userId ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        groupService.changeGroupMemberRole(groupId, targetUsername, role, userId)
        return ResponseEntity.ok().build()
    }
}
