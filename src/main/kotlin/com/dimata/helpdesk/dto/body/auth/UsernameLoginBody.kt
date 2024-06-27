package com.dimata.helpdesk.dto.body.auth

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class UsernameLoginBody(
    @field:NotNull
    @field:NotEmpty
    val username: String?,

    @field:NotNull
    @field:NotEmpty
    val password: String?,

    @field:NotNull
    @field:NotEmpty
    val ipAddress: String?
)