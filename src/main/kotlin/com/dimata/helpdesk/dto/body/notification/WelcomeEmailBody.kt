package com.dimata.helpdesk.dto.body.notification

data class WelcomeEmailBody(
    val fullName: String,
    val username: String,
    val roles: String?,
    val email: String? = null,
    val password: String? = "password123"
)