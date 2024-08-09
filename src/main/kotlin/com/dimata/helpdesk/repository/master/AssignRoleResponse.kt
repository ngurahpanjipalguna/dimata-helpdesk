package com.dimata.helpdesk.repository.master

import com.dimata.helpdesk.dto.response.DropdownResponse

data class AssignedRoleResponse(
    val roleId: String,
    val name: String,
    val permissions: List<DropdownResponse>
)