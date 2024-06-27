package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class TicketBody(
    @field:NotNull
    val registrationNumber: String?,

    @field:NotNull
    val registrationDateTime: LocalDateTime?,

    @field:NotNull
    val subject: String?,

    @field:NotNull
    val description: String?,

    @field:NotNull
    val priorityId: Long?,

    @field:NotNull
    val serviceLevel: String?,

    @field:NotNull
    val customerId: String?,

    @field:NotNull
    val providerId: Long?,

    @field:NotNull
    val masterStatusId: Long?,

    @field:NotNull
    val customerTeamId: Long?
)
