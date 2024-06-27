package com.dimata.helpdesk.dto.custom.auth

import java.time.LocalDateTime

data class SessionIdAndRefreshTokenOut(
        val sessionId: String,
        val refreshToken: String,
        val expiredAt: LocalDateTime
)