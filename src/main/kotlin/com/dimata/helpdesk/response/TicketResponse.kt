package com.dimata.helpdesk.response

import java.time.LocalDateTime

data class TicketResponse(
    val id: String,
    val description: String,
    val registrationDateTime: LocalDateTime?,
    val subject: String?,
    val serviceLevel:String,
    val customerId: Long?,
    val customerName:String?,
    val customerTeamId: Long,
    val createdAt : LocalDateTime?,
    val updatedAt: LocalDateTime?
)
