package com.grepp.nbe1_3_team9.schedule.controller.dto

import java.io.Serializable

data class UpdatedData(
    val pinId: Long,
    val description: String,
    val visitStart: String,
    val visitEnd: String
) : Serializable
