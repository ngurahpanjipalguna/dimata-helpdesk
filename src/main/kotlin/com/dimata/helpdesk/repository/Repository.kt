package com.dimata.helpdesk.repository

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectLimitStep

@ApplicationScoped
class Repository {

    @Inject
    lateinit var jooq: DSLContext

    fun <R : Record?> SelectLimitStep<R>.paginate(limit: Int, page: Int) =
        limit(limit)
            .offset(page * limit)

}