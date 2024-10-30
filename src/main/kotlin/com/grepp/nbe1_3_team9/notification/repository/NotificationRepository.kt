package com.grepp.nbe1_3_team9.notification.repository

import com.grepp.nbe1_3_team9.notification.entity.Notification
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class NotificationRepository(
    @Qualifier("notificationRedisTemplate") private val notificationRedisTemplate: RedisTemplate<String, Notification>
) {
    companion object {
        private const val KEY_PREFIX = "notification:user:"
    }

    fun save(notification: Notification) {
        val key = KEY_PREFIX + notification.receiverId
        notificationRedisTemplate.opsForHash<String, Notification>().put(key, notification.id!!, notification)
    }

    fun findByIdAndReceiverId(notificationId: String, receiverId: Long): Notification? {
        val key = KEY_PREFIX + receiverId
        return notificationRedisTemplate.opsForHash<String, Notification>().get(key, notificationId)
    }

    fun findByReceiverIdAndReadFalse(receiverId: Long): List<Notification> {
        val key = KEY_PREFIX + receiverId
        return notificationRedisTemplate.opsForHash<String, Notification>().values(key)
            .filter { !it.read }
    }

    fun findByReceiverId(receiverId: Long): List<Notification> {
        val key = KEY_PREFIX + receiverId
        return notificationRedisTemplate.opsForHash<String, Notification>().values(key)
    }

    fun countByReceiverIdAndReadFalse(receiverId: Long): Long {
        val key = KEY_PREFIX + receiverId
        return notificationRedisTemplate.opsForHash<String, Notification>().values(key)
            .count { !it.read }
            .toLong()
    }

    fun markAllAsReadBulk(receiverId: Long) {
        val key = KEY_PREFIX + receiverId
        val notifications = notificationRedisTemplate.opsForHash<String, Notification>().entries(key)

        val updatedNotifications = notifications.mapValues { (_, notification) ->
            if (!notification.read && notification.type != "INVITE") {
                notification.copy(read = true)
            } else {
                notification
            }
        }

        notificationRedisTemplate.opsForHash<String, Notification>().putAll(key, updatedNotifications)
    }
}
