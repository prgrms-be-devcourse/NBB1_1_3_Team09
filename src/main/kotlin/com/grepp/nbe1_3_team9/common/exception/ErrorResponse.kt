package com.grepp.nbe1_3_team9.common.exception

data class ErrorResponse(
    val status: Int,
    val title: String,
    val message: String
) {
    companion object {
        fun from(status: Int, title: String, message: String): ErrorResponse {
            return ErrorResponse(status, title, message)
        }
    }
}