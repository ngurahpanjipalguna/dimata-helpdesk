package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.StatusBody
import com.dimata.helpdesk.repository.StatusRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/api/v1/master")
class StatusController(
    private val statusRepository: StatusRepository
) {

    @GET
    @Path("/status")
    fun getAll(@QueryParam("page") page: Int?, @QueryParam("limit") limit: Int?): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val status = statusRepository.getAll(pageValue, limitValue)
        return Response.ok(status).build()
    }


    @GET
    @Path("/status/{id}")
    fun getById(@RestPath id: String): Response {
        return try {
            val status = statusRepository.getById(id)
            Response.ok(status).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }

    }

    @POST
    @Transactional
    @Path("/status")
    fun create(@Valid body: StatusBody): BaseResponse {
        val id = statusRepository.create(body)
        return BaseResponse(
            message = "Status created",
            data = id
        )
    }

    @PUT
    @Transactional
    @Path("/status/{id}")
    fun update(@RestPath id: String, @Valid body: StatusBody): BaseResponse {
        statusRepository.update(body, id)
        return BaseResponse(
            message = "Status updated",
            data = id
        )
    }

    @DELETE
    @Path("/status/{id}")
    @Transactional
    fun delete(@RestPath id: String): BaseResponse {
        statusRepository.softDelete(id)
        return BaseResponse(
            message = "Status deleted",
            data = null
        )
    }


}
