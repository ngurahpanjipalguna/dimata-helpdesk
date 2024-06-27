package com.dimata.helpdesk.dto.response.master

data class UserDetailResponse(
    val userId: String,
    val username: String,
    val email: String?,
    val fullName: String,
    val maxLoginAttempt: Int
)