package com.dimata.helpdesk.response

import com.dimata.helpdesk.gen.tables.MasterType
import java.time.LocalDateTime

data class TypeResponse(
    val id: String?,
    val jhiGroup: String? ,
    val title: String? ,
    val description: String? ,
    val level: String? ,
    val levelSign :String?,
    val defaulRequired : Boolean?,
    val providerId: Int?,
    val parentMasterTypeId : Int?,
    val operatorId : Int?,
    val userId : Int ?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?

)