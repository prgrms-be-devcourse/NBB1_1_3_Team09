package com.grepp.nbe1_3_team9.notification.controller.dto

data class NotificationDto(
    val type: String,
    val message: String,
    val senderId: Long?,
    val receiverId: Long?,
    val invitationId: Long?
)
