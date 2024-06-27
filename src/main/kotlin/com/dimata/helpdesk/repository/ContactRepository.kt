package com.dimata.helpdesk.repository

import com.dimata.helpdesk.ContactBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.ContactClass.CONTACT_CLASS
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.ContactResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import java.time.LocalDateTime

@ApplicationScoped
class ContactRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<ContactResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(CONTACT_CLASS)
            .where(CONTACT_CLASS.DELETED_AT.isNull)
            .orderBy(CONTACT_CLASS.ID.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(ContactResponse::class.java)
    }


    fun create(body: ContactBody): String =
        jooq.newRecord(CONTACT_CLASS, body)
            .also { contact ->
                contact.id= generateTSID()
                contact.type=body.type
                contact.classType=body.classType
                contact.operatorId=body.operatorId
                contact.providerId=body.providerId
                contact.createdAt = LocalDateTime.now()
                contact.updatedAt = LocalDateTime.now()
                contact.store()
            }
            .id

    fun update(body: ContactBody, id: String): String =
        (jooq.fetchOne(Tables.CONTACT_CLASS, Tables.CONTACT_CLASS.ID.eq(id))
            ?: throw RuntimeException("Product with id $id not found"))
            .also {
                it.providerId= body.providerId
                it.classType = body.classType
                it.type=body.type
                it.updatedAt = LocalDateTime.now()
                it.store()
            }
            .id

    fun softDelete(id: String) =
        jooq.update(Tables.CONTACT_CLASS)
            .set(Tables.CONTACT_CLASS.DELETED_AT, LocalDateTime.now())
            .where(Tables.CONTACT_CLASS.ID.eq(id))
            .execute()

    fun getById(id: String): ContactResponse? {
        return jooq.select()
            .from(Tables.CONTACT_CLASS)
            .where(Tables.CONTACT_CLASS.ID.eq(id))
            .and(Tables.CONTACT_CLASS.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(ContactResponse::class.java)
            ?: throw NotFoundException("Contact with id $id not found")
    }

}
