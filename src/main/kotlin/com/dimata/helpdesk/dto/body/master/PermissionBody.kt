package com.dimata.helpdesk.dto.body.master

import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.Length
import org.jetbrains.annotations.NotNull

data class PermissionBody(
    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 50)
    val name: String?,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 50)
    val description: String?
)