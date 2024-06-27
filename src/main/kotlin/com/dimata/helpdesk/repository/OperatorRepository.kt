package com.dimata.helpdesk.repository

import com.dimata.helpdesk.OperatorBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.Operator.OPERATOR
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.OperatorResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import java.time.LocalDateTime

@ApplicationScoped
class OperatorRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<OperatorResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(OPERATOR)
            .where(OPERATOR.DELETED_AT.isNull)
            .orderBy(OPERATOR.UPDATED_AT.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(OperatorResponse::class.java)
    }

    fun getById(id: String): OperatorResponse? {
        return jooq.select()
            .from(Tables.OPERATOR)
            .where(Tables.OPERATOR.ID.eq(id))
            .and(Tables.OPERATOR.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(OperatorResponse::class.java)
            ?: throw NotFoundException("Contact with id $id not found")
    }


    fun create(body: OperatorBody): String =
        jooq.newRecord(OPERATOR, body)
            .also { operator ->
                operator.id= generateTSID()
                operator.name=body.name
                operator.userSize=body.userSize
                operator.companySize=body.companySize
                operator.providerSize=body.providerSize
                operator.serviceLevel=body.serviceLevel
                operator.createdAt=LocalDateTime.now()
                operator.updatedAt=LocalDateTime.now()
                operator.regDatetime=body.registrationDateTime
                operator.registrationStatus=body.registrationStatus
                operator.store()
            }
            .id
    fun update(body: OperatorBody, id: String): String =
        (jooq.fetchOne(Tables.OPERATOR, Tables.OPERATOR.ID.eq(id))
            ?: throw RuntimeException("Operator with id $id not found"))
            .also {
                it.name= body.name
                it.userSize = body.userSize
                it.companySize = body.companySize
                it.serviceLevel=body.serviceLevel
                it.regDatetime=body.registrationDateTime
                it.registrationStatus=body.registrationStatus
                it.providerSize=body.providerSize
                it.updatedAt = LocalDateTime.now()
                it.store()
            }
            .id

    fun softDelete(id: String) =
        jooq.update(Tables.OPERATOR)
            .set(Tables.OPERATOR.DELETED_AT, LocalDateTime.now())
            .where(Tables.OPERATOR.ID.eq(id))
            .execute()


}