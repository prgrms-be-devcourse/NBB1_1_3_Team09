package com.grepp.nbe1_3_team9.notification.entity

import jakarta.persistence.Id
import java.io.Serializable
import java.time.LocalDateTime

data class Notification(
    @Id
    val id: String? = null,
    val type: String,
    val message: String,
    val senderId: Long?,
    val receiverId: Long?,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var read: Boolean = false,
    val invitationId: Long?
) : Serializable {
    fun markAsRead() {
        this.read = true
    }
}