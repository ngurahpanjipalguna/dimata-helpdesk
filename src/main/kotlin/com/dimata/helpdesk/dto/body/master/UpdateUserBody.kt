package com.dimata.helpdesk.dto.body.master

import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length
import org.jetbrains.annotations.NotNull

data class UpdateUserBody(
    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 50)
    val username: String?,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 50)
    val email: String?,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 50)
    val fullName: String?,

    val maxLoginAttempt: Int?
)