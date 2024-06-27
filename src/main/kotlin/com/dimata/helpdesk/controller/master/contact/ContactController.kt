package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.ContactBody
import com.dimata.helpdesk.repository.ContactRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath

@Path("/api/v1/master")
class ContactController(
    private val contactRepository: ContactRepository
) {

    @GET
    @Path("/contact")
    fun getAll(@QueryParam("page") page: Int?, @QueryParam("limit") limit: Int?): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val contacts = contactRepository.getAll(pageValue, limitValue)
        return Response.ok(contacts).build()
    }

    @POST
    @Transactional
    @Path("/contact")
    fun create(@Valid body: ContactBody): BaseResponse {
        val id = contactRepository.create(body)
        return BaseResponse(
            message = "Contact created",
            data = id
        )
    }

    @PUT
    @Transactional
    @Path("/contact/{id}")
    fun update(@RestPath id: String, @Valid body: ContactBody): BaseResponse {
        contactRepository.update(body, id)
        return BaseResponse(
            message = "Product updated",
            data = id
        )
    }

    @DELETE
    @Path("/contact/{id}")
    @Transactional
    fun delete(@RestPath id: String): BaseResponse {
        contactRepository.softDelete(id)
        return BaseResponse(
            message = "Product deleted",
            data = null
        )
    }

    @GET
    @Path("/contact/{id}")
    fun getById(@RestPath id: String): Response {
        return try {
            val contact = contactRepository.getById(id)
            Response.ok(contact).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }


    }
}
