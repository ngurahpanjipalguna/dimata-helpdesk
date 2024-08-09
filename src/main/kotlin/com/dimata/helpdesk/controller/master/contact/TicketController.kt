package com.dimata.helpdesk.controller.master.contact


import com.dimata.helpdesk.TicketBody
import com.dimata.helpdesk.core.auth.permission.Permission
import com.dimata.helpdesk.repository.TicketRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

@Path("/api/v1/master")
class TicketController(
        private val ticketRepository: TicketRepository
) {

    @GET
    @Path("/ticket")
    @Permission("view_ticket")
    fun getAll(
        @QueryParam("page") page: Int?,
        @QueryParam("limit") limit: Int?,
        @QueryParam("service-level") service_level: String?,
        @QueryParam("start-date") startDateStr: String?,
        @QueryParam("end-date") endDateStr: String?
    ): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided

        val startDate = startDateStr?.trim()?.let {
            try {
                LocalDateTime.parse(it)
            } catch (e: DateTimeParseException) {
                throw BadRequestException("Invalid start-date format. Expected format: yyyy-MM-dd'T'HH:mm:ss")
            }
        }

        val endDate = endDateStr?.trim()?.let {
            try {
                LocalDateTime.parse(it)
            } catch (e: DateTimeParseException) {
                throw BadRequestException("Invalid end-date format. Expected format: yyyy-MM-dd'T'HH:mm:ss")
            }
        }

        val tickets = ticketRepository.getAll(pageValue, limitValue, service_level, startDate, endDate)
        return Response.ok(tickets).build()
    }


    @POST
    @Transactional
    @Path("/ticket")
    @Permission("create_ticket")
    fun create(@Valid body: TicketBody): BaseResponse {
        val id = ticketRepository.create(body)
        return BaseResponse(
            message = "Ticket created",
            data = id
        )
    }

    @PUT
    @Transactional
    @Path("/ticket/{id}")
    @Permission("update_ticket")
    fun update(@RestPath id: String, @Valid body: TicketBody): BaseResponse {
        ticketRepository.update(body, id)
        return BaseResponse(
            message = "Ticket updated",
            data = id
        )
    }

    @GET
    @Path("/ticket/{id}")
    @Permission("view_ticket")
    fun getById(@RestPath id: String): Response {
        return try {
            val ticket = ticketRepository.getById(id)
            Response.ok(ticket).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }

    }

    @DELETE
    @Path("/ticket/{id}")
    @Transactional
    @Permission("delete_ticket")
    fun delete(@RestPath id: String): BaseResponse {
        ticketRepository.softDelete(id)
        return BaseResponse(
            message = "Ticket deleted",
            data = null
        )
    }

}
