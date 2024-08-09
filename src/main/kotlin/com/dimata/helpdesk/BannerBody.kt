package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull

data class BannerBody(
    @field:NotNull
    val bannerUrl: String?,

    @field:NotNull
    val operatorId: Int?,
)
