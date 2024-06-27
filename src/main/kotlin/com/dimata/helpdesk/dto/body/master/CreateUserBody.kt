package com.dimata.helpdesk.dto.body.master

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class CreateUserBody(
    @field:NotNull
    @field:NotEmpty
    @field:Pattern(regexp = "^[a-z0-9_]+\$", message = "must contain only lowercase letters, numbers, and underscores")
    val username: String?,

    val email: String?,

    @field:NotNull
    @field:NotEmpty
    val fullName: String?,

    @field:NotNull
    val maxLoginAttempt: Int?,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 5, max = 100)
    val password: String?,

    val rolesId: Set<String>?,

    val xkEmployeeId: Long? = null,
    val xkPositionId: Long? = null,
    val positionName: String? = null
)