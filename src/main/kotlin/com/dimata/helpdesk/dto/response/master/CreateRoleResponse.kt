package com.dimata.helpdesk.dto.response.master

data class CreateRoleResponse(
    val roleId: String,
    val permissions: com.dimata.helpdesk.dto.body.master.AssignPermissionOut?
)