package com.dimata.helpdesk.dto.custom.master

data class AssignPermissionOut(
    val permissionAssigned: Set<String>,
    val permissionNotFound: Set<String>
)