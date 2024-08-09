package com.dimata.helpdesk.dto.body.master

data class AssignPermissionOut(
    val permissionAssigned: Set<String>,
    val permissionNotFound: Set<String>
)