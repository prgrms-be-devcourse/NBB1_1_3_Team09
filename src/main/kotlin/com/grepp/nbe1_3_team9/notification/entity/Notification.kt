package com.grepp.nbe1_3_team9.notification.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    val id: String = UUID.randomUUID().toString(),

    val type: String,
    val message: String,
    val senderId: Long,
    val receiverId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var isRead: Boolean = false,
    val invitationId: Long
) : Serializable {
    fun markAsRead() {
        this.isRead = true
    }
}
