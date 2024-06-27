package com.dimata.helpdesk.dto.body.auth

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class RefreshTokenLoginBody(
    @field:NotNull
    @field:NotEmpty
    val refreshToken: String?,

    @field:NotNull
    @field:NotEmpty
    val ipAddress: String?
)