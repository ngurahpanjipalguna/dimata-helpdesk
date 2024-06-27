package com.dimata.helpdesk.response

data class BaseResponse(
    val status: String = "SUCCESS",
    val message: String,
    val data: Any?
)
