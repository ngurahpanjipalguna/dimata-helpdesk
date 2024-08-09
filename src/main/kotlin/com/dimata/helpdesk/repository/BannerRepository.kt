package com.dimata.helpdesk.repository

import com.dimata.helpdesk.BannerBody
import com.dimata.helpdesk.gen.Tables
import com.dimata.helpdesk.gen.tables.Banner.BANNER
import com.dimata.helpdesk.generator.generateTSID
import com.dimata.helpdesk.response.BannerResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import java.time.LocalDateTime

@ApplicationScoped
class BannerRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<BannerResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(BANNER)
            .where(BANNER.DELETED_AT.isNull)
            .orderBy(BANNER.ID.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(BannerResponse::class.java)
    }

    fun getById(id: String): BannerResponse? {
        return jooq.select()
            .from(Tables.BANNER)
            .where(Tables.BANNER.ID.eq(id))
            .limit(1)
            .fetchOneInto(BannerResponse::class.java)
            ?: throw NotFoundException("Contact with id $id not found")
    }

    fun create(body: BannerBody): String =
        jooq.newRecord(BANNER, body)
            .also { banner ->
                banner.id= generateTSID()
                banner.operatorId=body.operatorId
                banner.bannerUrl=body.bannerUrl
                banner.createdAt=LocalDateTime.now()
                banner.updatedAt=LocalDateTime.now()
                banner.store();
            }
            .id

    fun update(body: BannerBody, id: String): String =
        (jooq.fetchOne(Tables.BANNER, Tables.BANNER.ID.eq(id))
            ?: throw RuntimeException("Operator with id $id not found"))
            .also {
                it.bannerUrl= body.bannerUrl
                it.operatorId = body.operatorId
                it.updatedAt=LocalDateTime.now()
                it.store()
            }
            .id

    fun softDelete(id: String) =
        jooq.update(Tables.BANNER)
            .set(Tables.BANNER.DELETED_AT, LocalDateTime.now())
            .where(Tables.BANNER.ID.eq(id))
            .execute()

}
