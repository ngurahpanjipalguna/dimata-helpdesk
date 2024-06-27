package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.OperatorBody
import com.dimata.helpdesk.TicketBody
import com.dimata.helpdesk.TypeBody
import com.dimata.helpdesk.repository.TicketRepository
import com.dimata.helpdesk.repository.TypeRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/api/v1/master")
class TypeController(
    private val typeRepository: TypeRepository
) {

    @GET
    @Path("/type")
    fun getAll(
        @QueryParam("page") page: Int?,
        @QueryParam("limit") limit: Int?,
        @QueryParam("service-level") service_level: String?
    ): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val types = typeRepository.getAll(pageValue, limitValue)
        return Response.ok(types).build()
    }

    @POST
    @Transactional
    @Path("/type")
    fun create(@Valid body: TypeBody): BaseResponse {
        val id = typeRepository.create(body)
        return BaseResponse(
            message = "Type created",
            data = id
        )
    }

    @GET
    @Path("/type/{id}")
    fun getById(@RestPath id: String): Response {
        return try {
            val type = typeRepository.getById(id)
            Response.ok(type).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }

    }

    @PUT
    @Transactional
    @Path("/type/{id}")
    fun update(@RestPath id: String, @Valid body: TypeBody): BaseResponse {
        typeRepository.update(body, id)
        return BaseResponse(
            message = "Type updated",
            data = id
        )
    }

    @DELETE
    @Path("/type/{id}")
    @Transactional
    fun delete(@RestPath id: String): BaseResponse {
        typeRepository.softDelete(id)
        return BaseResponse(
            message = "Type deleted",
            data = null
        )
    }
}