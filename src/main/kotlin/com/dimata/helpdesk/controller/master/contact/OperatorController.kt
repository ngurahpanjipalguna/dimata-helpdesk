package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.OperatorBody
import com.dimata.helpdesk.repository.OperatorRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/api/v1/master")
class OperatorController(
    private val operatorRepository: OperatorRepository
) {

    @GET
    @Path("/operator")
    fun getAll(@QueryParam("page") page: Int?, @QueryParam("limit") limit: Int?): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val contacts = operatorRepository.getAll(pageValue, limitValue)
        return Response.ok(contacts).build()
    }


    @GET
    @Path("/operator/{id}")
    fun getById(@RestPath id: String): Response {
        return try {
            val operator = operatorRepository.getById(id)
            Response.ok(operator).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }

    }

    @POST
    @Transactional
    @Path("/operator")
    fun create(@Valid body: OperatorBody): BaseResponse {
        val id = operatorRepository.create(body)
        return BaseResponse(
            message = "Operator created",
            data = id
        )
    }

    @PUT
    @Transactional
    @Path("/operator/{id}")
    fun update(@RestPath id: String, @Valid body: OperatorBody): BaseResponse {
        operatorRepository.update(body, id)
        return BaseResponse(
            message = "Operator updated",
            data = id
        )
    }

    @DELETE
    @Path("/operator/{id}")
    @Transactional
    fun delete(@RestPath id: String): BaseResponse {
        operatorRepository.softDelete(id)
        return BaseResponse(
            message = "Operator deleted",
            data = null
        )
    }


}
