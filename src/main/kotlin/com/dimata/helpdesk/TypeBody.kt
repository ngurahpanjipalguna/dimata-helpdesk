package com.dimata.helpdesk

import jakarta.validation.constraints.NotNull

data class TypeBody(
    @field:NotNull
    val jhiGroup: String?,

    @field:NotNull
    val title: String?,

    @field:NotNull
    val description: String?,

    @field:NotNull
    val level:Int?,

    @field:NotNull
    val levelSign:String?,

    @field:NotNull
    val defaultRequired:Byte?,


    @field:NotNull
    val providerId:Long?,

    @field:NotNull
    val parentMasterTypeId:Long?,

    @field:NotNull
    val operatorId:Long?,

    @field:NotNull
    val userId:Long?
)
