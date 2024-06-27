package com.dimata.helpdesk.dto.response

data class BaseResponse(
    val status: String = "SUCCESS",
    val message: String,
    val data: Any?
)