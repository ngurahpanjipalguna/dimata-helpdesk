package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class OperatorBody(
    @field:NotNull
    val name: String?,

    @field:NotNull
    val registrationDateTime: LocalDateTime?,

    @field:NotNull
    val registrationStatus: String?,

    @field:NotNull
    val companySize:Int?,

    @field:NotNull
    val userSize:Int?,

    @field:NotNull
    val providerSize:Int?,

    @field:NotNull
    val serviceLevel:String?,

    @field:NotNull
    val contactListId:Long?,
)
