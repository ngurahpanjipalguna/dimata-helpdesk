package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class ContactBody(
    @field:NotNull
    val type: String?,

    @field:NotNull
    val classType: String?,

    @field:NotNull
    val operatorId: Long?,

    @field:NotNull
    val providerId:Long?,
)
