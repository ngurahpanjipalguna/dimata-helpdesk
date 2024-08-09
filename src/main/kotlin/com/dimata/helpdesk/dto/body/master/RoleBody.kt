package com.dimata.helpdesk.dto.body.master

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length

data class RoleBody(
    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 100)
    val name: String?,

    @field:NotNull
    @field:NotEmpty
    @field:Length(min = 1, max = 100)
    val description: String?,

    val permissionsId: Set<String>? = null,

    val xkPositionId: Long? = null
)