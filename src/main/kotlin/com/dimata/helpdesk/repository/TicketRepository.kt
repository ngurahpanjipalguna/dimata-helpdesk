package com.dimata.helpdesk.repository

import com.dimata.helpdesk.TicketBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.Customer.CUSTOMER
import com.dimata.helpdesk.gen.tables.Ticket.TICKET
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.TicketResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import java.time.LocalDateTime

@ApplicationScoped
class TicketRepository(
    private val jooq: DSLContext
) {
    fun getAll(
        page: Int,
        limit: Int,
        serviceLevel: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): MutableList<TicketResponse> {
        val offset = (page - 1) * limit
        return try {
            val query = jooq.select(
                TICKET.ID,
                TICKET.DESCRIPTION,
                TICKET.CREATED_AT.`as`("createdAt"),
                TICKET.REG_DATETIME.`as`("registrationDateTime"),
                TICKET.SUBJECT,
                TICKET.SERVICE_LEVEL,
                TICKET.CUSTOMER_ID.`as` ("customerId"),
                CUSTOMER.NAME.`as`("customerName"),
                TICKET.CUSTOMER_TEAM_ID,
                TICKET.UPDATED_AT.`as`("updatedAt")
            )
                .from(TICKET)
                .leftJoin(CUSTOMER).on(TICKET.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID)) // Adjust join condition
                .where(TICKET.DELETED_AT.isNull)

            // Apply filters if present
            if (!serviceLevel.isNullOrBlank()) {
                query.and(TICKET.SERVICE_LEVEL.like("%$serviceLevel%"))
            }

            if (startDate != null) {
                query.and(TICKET.CREATED_AT.ge(startDate))
            }

            if (endDate != null) {
                query.and(TICKET.CREATED_AT.le(endDate))
            }

            query.orderBy(TICKET.UPDATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(TicketResponse::class.java)
        } catch (e: DataAccessException) {
            // Handle the exception or rethrow it as a custom exception
            throw RuntimeException("Error fetching tickets", e)
        }
    }



    fun create(body: TicketBody): String =
        jooq.newRecord(TICKET, body)
            .also { ticket ->
                ticket.id= generateTSID()
                ticket.regDatetime=body.registrationDateTime
                ticket.registrationNumber=body.registrationNumber
                ticket.masterStatusId=body.masterStatusId
                ticket.providerId=body.providerId
                ticket.customerId=body.customerId
                ticket.subject=body.subject
                ticket.serviceLevel=body.serviceLevel
                ticket.createdAt=LocalDateTime.now()
                ticket.updatedAt=LocalDateTime.now()
                ticket.store()
            }
            .id

    fun update(body: TicketBody, id: String): String =
        (jooq.fetchOne(Tables.TICKET, Tables.TICKET.ID.eq(id))
            ?: throw RuntimeException("Ticket with id $id not found"))
            .also {
                it.regDatetime=body.registrationDateTime
                it.registrationNumber=body.registrationNumber
                it.masterStatusId=body.masterStatusId
                it.providerId=body.providerId
                it.customerId=body.customerId
                it.subject=body.subject
                it.serviceLevel=body.serviceLevel
                it.updatedAt=LocalDateTime.now()
                it.store()
            }
            .id


    fun softDelete(id: String) =
        jooq.update(Tables.TICKET)
            .set(Tables.TICKET.DELETED_AT, LocalDateTime.now())
            .where(Tables.TICKET.ID.eq(id))
            .execute()

    fun getById(id: String): TicketResponse? {
        return jooq.select()
            .from(Tables.TICKET)
            .where(Tables.TICKET.ID.eq(id))
            .and(Tables.TICKET.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(TicketResponse::class.java)
            ?: throw NotFoundException("Ticket with id $id not found")
    }

}