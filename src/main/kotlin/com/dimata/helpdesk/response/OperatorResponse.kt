package com.dimata.helpdesk.response

import java.time.LocalDateTime

data class OperatorResponse (
    val id : String,
    val name : String?,
    val registrationDateTime : LocalDateTime?,
    val registrationStatus: String?,
    val companySize: Long?,
    val userSize: Long?,
    val providerSize: Long?,
    val serviceLevel : Long?,
    val contactListId: Long?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)