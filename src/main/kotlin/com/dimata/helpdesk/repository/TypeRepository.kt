package com.dimata.helpdesk.repository

import com.dimata.helpdesk.TypeBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.MasterType.MASTER_TYPE
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.TypeResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import java.time.LocalDateTime

@ApplicationScoped
class TypeRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<TypeResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(MASTER_TYPE)
            .where(MASTER_TYPE.DELETED_AT.isNull)
            .orderBy(MASTER_TYPE.ID.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(TypeResponse::class.java)
    }

    fun create(body: TypeBody): String =
        jooq.newRecord(MASTER_TYPE, body)
            .also {
                it.id= generateTSID()
                it.createdAt=LocalDateTime.now()
                it.updatedAt=LocalDateTime.now()
                it.providerId=body.providerId
                it.userId=body.userId
                it.operatorId=body.operatorId
                it.jhiGroup=body.jhiGroup
                it.level=body.level
                it.levelSign=body.levelSign
                it.description=body.description
                it.parentMasterTypeId=body.parentMasterTypeId
                it.defaultRequired=body.defaultRequired
                it.store()
            }
            .id

    fun getById(id: String): TypeResponse? {
        return jooq.select()
            .from(Tables.MASTER_TYPE)
            .where(Tables.MASTER_TYPE.ID.eq(id))
            .and(Tables.MASTER_TYPE.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(TypeResponse::class.java)
            ?: throw NotFoundException("Ticket with id $id not found")
    }

    fun update(body: TypeBody, id: String): String =
        (jooq.fetchOne(Tables.MASTER_TYPE, Tables.MASTER_TYPE.ID.eq(id))
            ?: throw RuntimeException("Ticket with id $id not found"))
            .also {
                it.createdAt=LocalDateTime.now()
                it.updatedAt=LocalDateTime.now()
                it.providerId=body.providerId
                it.userId=body.userId
                it.operatorId=body.operatorId
                it.jhiGroup=body.jhiGroup
                it.level=body.level
                it.levelSign=body.levelSign
                it.description=body.description
                it.parentMasterTypeId=body.parentMasterTypeId
                it.defaultRequired=body.defaultRequired
                it.store()
            }
            .id

    fun softDelete(id: String) =
        jooq.update(Tables.MASTER_TYPE)
            .set(Tables.MASTER_TYPE.DELETED_AT, LocalDateTime.now())
            .where(Tables.MASTER_TYPE.ID.eq(id))
            .execute()


}



