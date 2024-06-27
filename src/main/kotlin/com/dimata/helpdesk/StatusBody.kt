package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull

data class StatusBody(
    @field:NotNull
    val title: String?,

    @field:NotNull
    val description: String?
)
