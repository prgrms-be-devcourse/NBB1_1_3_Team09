package com.grepp.nbe1_3_team9.notification.controller

import com.grepp.nbe1_3_team9.admin.service.CustomUserDetails
import com.grepp.nbe1_3_team9.common.exception.ExceptionMessage
import com.grepp.nbe1_3_team9.common.exception.exceptions.UserException
import com.grepp.nbe1_3_team9.notification.controller.dto.NotificationDto
import com.grepp.nbe1_3_team9.notification.controller.dto.NotificationResp
import com.grepp.nbe1_3_team9.notification.entity.Notification
import com.grepp.nbe1_3_team9.notification.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime
import java.util.*

@Controller
class NotificationController(private val notificationService: NotificationService) {

    @MessageMapping("/invite")
    fun sendInvitation(notificationDto: NotificationDto) {
        val responseResp = createNotificationResp(notificationDto)
        notificationService.sendNotificationAsync(responseResp)
    }

    @MessageMapping("/inviteResponse")
    fun sendInvitationResponse(notificationDto: NotificationDto) {
        val responseResp = createNotificationResp(notificationDto)
        notificationService.sendNotificationAsync(responseResp)
    }

    @GetMapping("/notifications")
    fun getNotifications(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(defaultValue = "false") unreadOnly: Boolean
    ): ResponseEntity<List<Notification>> {
        val userId = userDetails.getUserId() ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        return ResponseEntity.ok(notificationService.getNotifications(userId, unreadOnly))
    }

    @GetMapping("/notifications/unread-count")
    fun getUnreadCount(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Int> {
        val userId = userDetails.getUserId() ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        return ResponseEntity.ok(notificationService.getUnreadCount(userId))
    }

    @PostMapping("/notifications/{notificationId}/read")
    fun markNotificationAsRead(
        @PathVariable notificationId: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Unit> {
        val userId = userDetails.getUserId() ?: throw UserException(ExceptionMessage.USER_ID_NULL)
        notificationService.markAsRead(notificationId, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/notifications/mark-all-read")
    fun markAllNotificationsAsRead(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<Unit> {
        val userId = userDetails.getUserId() ?:throw UserException(ExceptionMessage.USER_ID_NULL)
        notificationService.markAllAsRead(userId)
        return ResponseEntity.ok().build()
    }

    private fun createNotificationResp(notificationDto: NotificationDto): NotificationResp {
        return NotificationResp(
            UUID.randomUUID().toString(),
            notificationDto.type,
            notificationDto.message,
            notificationDto.senderId,
            notificationDto.receiverId,
            notificationDto.invitationId,
            LocalDateTime.now().toString(),
            false
        )
    }
}
