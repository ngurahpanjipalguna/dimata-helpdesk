package com.dimata.helpdesk.dto.custom.auth

data class UserAndRolesOut(
    val userId: String,
    val username: String,
    val password: String,
    val fullName: String,
    val maxLoginAttempt: Int,
    val roles: Set<RoleOut>?,
)