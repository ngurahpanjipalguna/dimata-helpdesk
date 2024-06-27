package com.dimata.helpdesk.response

import java.time.LocalDateTime

data class StatusResponse (
    val id : String,
    val title : String?,
    val description : String?,
    val createdAt : LocalDateTime?,
    val updatedAt : LocalDateTime?,
)