package com.dimata.helpdesk.dto.response.auth

import java.time.LocalDateTime

data class LoginResponse(
    val sessionId: String,
    val userId: String,
    val username: String,
    val fullName: String,
    val roles: Set<String>?,
    val permissions: Set<String>?,
    val expiredAt: LocalDateTime,
    val refreshToken: String?,
    val accessToken: String
)