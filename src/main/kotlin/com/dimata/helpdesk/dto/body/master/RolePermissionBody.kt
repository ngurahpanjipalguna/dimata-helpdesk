package com.dimata.helpdesk.dto.body.master

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class RolePermissionBody(
    @field:NotNull
    @field:NotEmpty
    val roleId: String?,

    @field:NotNull
    @field:NotEmpty
    val permissionsId: Set<String>?
)