package com.dimata.helpdesk.repository

import com.dimata.helpdesk.gen.tables.Benefit.BENEFIT
import com.dimata.helpdesk.response.BenefitResponse
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.DSLContext

@ApplicationScoped
class BenefitRepository(
    private val jooq: DSLContext
) {
    fun getAll(page: Int, limit: Int): MutableList<BenefitResponse> {
        val offset = (page - 1) * limit
        return jooq.selectFrom(
            BENEFIT)
            .orderBy(BENEFIT.ID.desc())
            .offset(offset)
            .limit(limit)
            .fetchInto(BenefitResponse::class.java)
    }




}
