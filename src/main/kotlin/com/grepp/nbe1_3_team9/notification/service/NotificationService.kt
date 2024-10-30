package com.grepp.nbe1_3_team9.notification.service

import com.grepp.nbe1_3_team9.notification.controller.dto.NotificationResp
import com.grepp.nbe1_3_team9.notification.entity.Notification
import com.grepp.nbe1_3_team9.notification.repository.NotificationRepository
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val messagingTemplate: SimpMessagingTemplate
) {
    @Async
    fun sendNotificationAsync(notificationResp: NotificationResp): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            try {
                sendNotification(notificationResp)
            } catch (e: Exception) {
                log.error("Failed to send notification: ${e.message}", e)
            }
        }
    }

    fun sendNotification(notificationResp: NotificationResp) {
        messagingTemplate.convertAndSend("/topic/user/${notificationResp.receiverId}", notificationResp)
        log.info("Sent notification to user: ${notificationResp.receiverId}")

        val notification = Notification(
            id = notificationResp.id,
            type = notificationResp.type,
            message = notificationResp.message,
            senderId = notificationResp.senderId,
            receiverId = notificationResp.receiverId,
            createdAt = LocalDateTime.parse(notificationResp.createdAt),
            read = notificationResp.read,
            invitationId = notificationResp.invitationId
        )
        log.info("보낸 사람: ${notificationResp.senderId}")
        log.info("Sent notification to user: ${notificationResp.receiverId}")
        notificationRepository.save(notification)
    }

    fun getNotifications(userId: Long, unreadOnly: Boolean): List<Notification> {
        return if (unreadOnly) {
            notificationRepository.findByReceiverIdAndReadFalse(userId)
        } else {
            notificationRepository.findByReceiverId(userId)
        }
    }

    fun getUnreadCount(userId: Long): Int {
        return notificationRepository.countByReceiverIdAndReadFalse(userId).toInt()
    }

    fun markAsRead(notificationId: String, userId: Long) {
        val notification = notificationRepository.findByIdAndReceiverId(notificationId, userId)
        if (notification != null) {
            val updatedNotification = notification.copy(read = true)
            notificationRepository.save(updatedNotification)

            val unreadCount = getUnreadCount(userId)
            messagingTemplate.convertAndSend("/topic/user/$userId/unreadCount", unreadCount)
        } else {
            log.warn("Notification not found or not belonging to user. ID: $notificationId, User: $userId")
        }
    }

    fun markAllAsRead(userId: Long) {
        notificationRepository.markAllAsReadBulk(userId)
        log.info("Marked all notifications as read for user: $userId")
        messagingTemplate.convertAndSend("/topic/user/$userId/unreadCount", 0)
    }

    companion object {
        private val log = org.slf4j.LoggerFactory.getLogger(NotificationService::class.java)
    }
}
