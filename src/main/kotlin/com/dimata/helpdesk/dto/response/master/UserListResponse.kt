package com.dimata.helpdesk.dto.response.master

data class UserListResponse(
    val userId: String,
    val username: String,
    val fullName: String,
    val maxLoginAttempt: Int,
    val roles: Set<String>?,
)