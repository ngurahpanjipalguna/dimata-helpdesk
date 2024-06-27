package com.dimata.helpdesk.dto.response.master

import java.time.LocalDateTime

data class UserMeResponse(
    val userId: String,
    val username: String,
    val fullName: String,
    val email: String?,
    val roles: Set<String>?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
