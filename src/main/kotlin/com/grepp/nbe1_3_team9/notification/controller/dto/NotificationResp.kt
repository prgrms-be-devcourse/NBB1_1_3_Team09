package com.grepp.nbe1_3_team9.notification.controller.dto

data class NotificationResp(
    val id: String,
    val type: String,
    val message: String,
    val senderId: Long?,
    val receiverId: Long?,
    val invitationId: Long?,
    val createdAt: String,
    val read: Boolean
)
