package com.dimata.helpdesk.dto.response.auth

import java.time.LocalDateTime

data class ActivityResponse(
    val userId: String?,
    val username: String?,
    val ipAddress: String?,
    val activity: String,
    val causedAt: LocalDateTime?
)