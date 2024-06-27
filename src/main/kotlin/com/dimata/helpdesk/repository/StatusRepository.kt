package com.dimata.helpdesk.repository

import com.dimata.helpdesk.StatusBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.MasterStatus.MASTER_STATUS
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.StatusResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import java.time.LocalDateTime

@ApplicationScoped
class StatusRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<StatusResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(MASTER_STATUS)
            .where(MASTER_STATUS.DELETED_AT.isNull)
            .orderBy(MASTER_STATUS.UPDATED_AT.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(StatusResponse::class.java)
    }

    fun getById(id: String): StatusResponse {
        return jooq.select()
            .from(Tables.MASTER_STATUS)
            .where(Tables.MASTER_STATUS.ID.eq(id))
            .and(Tables.MASTER_STATUS.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(StatusResponse::class.java)
            ?: throw NotFoundException("Status with id $id not found")
    }

    fun create(body: StatusBody): String {
        val id = generateTSID()
        jooq.insertInto(MASTER_STATUS)
            .set(MASTER_STATUS.ID, id)
            .set(MASTER_STATUS.TITLE, body.title)
            .set(MASTER_STATUS.DESCRIPTION, body.description)
            .set(MASTER_STATUS.CREATED_AT, LocalDateTime.now())
            .set(MASTER_STATUS.UPDATED_AT, LocalDateTime.now())
            .execute()
        return id
    }

    fun update(body: StatusBody, id: String): String {
        val rowsAffected = jooq.update(MASTER_STATUS)
            .set(MASTER_STATUS.TITLE, body.title)
            .set(MASTER_STATUS.DESCRIPTION, body.description)
            .set(MASTER_STATUS.UPDATED_AT, LocalDateTime.now())
            .where(MASTER_STATUS.ID.eq(id))
            .execute()

        if (rowsAffected == 0) {
            throw RuntimeException("Status with id $id not found")
        }
        return id
    }

    fun softDelete(id: String) {
        jooq.update(MASTER_STATUS)
            .set(MASTER_STATUS.DELETED_AT, LocalDateTime.now())
            .where(MASTER_STATUS.ID.eq(id))
            .execute()
    }
}
