package com.grepp.nbe1_3_team9.schedule.controller.dto

import java.io.Serializable

data class SavedData(
    val pinId: Long,
    val eventId: Long,
    val locationId: Long,
    val description: String,
    val visitStart: String,
    val visitEnd: String,
    val color: String
) : Serializable
