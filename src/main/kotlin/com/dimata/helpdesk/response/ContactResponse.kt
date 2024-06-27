package com.dimata.helpdesk.response

import java.time.LocalDateTime

data class ContactResponse(
    val id: String?,
    val type: String? ,
    val classType: String? ,
    val operatorId: String? ,
    val providerId: String? ,
    val createdAt: LocalDateTime? ,
    val updatedAt: LocalDateTime? ,
    val deletedAt: LocalDateTime? 

)