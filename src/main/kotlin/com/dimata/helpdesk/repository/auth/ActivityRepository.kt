package com.dimata.helpdesk.repository.auth

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.dto.param.ActivityParam
import com.dimata.helpdesk.dto.param.BaseParam
import com.dimata.helpdesk.dto.response.auth.ActivityResponse
import com.dimata.helpdesk.gen.Tables.*
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL
import java.time.LocalDate
import java.time.LocalDateTime

@ApplicationScoped
class ActivityRepository : Repository() {

    private val ACTIVITY = LOG_ACTIVITY.`as`("activity")

    fun create(
        event: String,
        causer: String? = null,
        sessionId: String? = null
    ): String =
        jooq.newRecord(ACTIVITY)
            .also {
                it.activityId = generateTSID()
                it.event = event
                it.activity = "$causer telah melakukan: $event"
                it.sessionId = sessionId
                it.createdAt = LocalDateTime.now()
                it.store()
            }
            .activityId

    fun getUserActivity(userId: String, param: BaseParam): MutableList<ActivityResponse> =
        jooq.select(
            ACTIVITY.session().user().FULL_NAME.`as`("username"),
            ACTIVITY.session().USER_ID.`as`("userId"),
            ACTIVITY.session().IP_ADDRESS.`as`("ipAddress"),
            ACTIVITY.ACTIVITY,
            ACTIVITY.CREATED_AT.`as`("causedAt")
        )
            .from(ACTIVITY)
            .where(ACTIVITY.session().USER_ID.eq(userId))
            .orderBy(ACTIVITY.CREATED_AT.desc())
            .paginate(page = param.page, limit = param.limit)
            .fetchInto(ActivityResponse::class.java)

    fun getActivity(param: ActivityParam): MutableList<ActivityResponse> =
        jooq.select(
            USER.FULL_NAME.`as`("username"),
            SESSION.USER_ID.`as`("userId"),
            SESSION.IP_ADDRESS.`as`("ipAddress"),
            ACTIVITY.ACTIVITY,
            ACTIVITY.CREATED_AT.`as`("causedAt")
        )
            .from(ACTIVITY)
            .leftJoin(SESSION).on(SESSION.SESSION_ID.eq(ACTIVITY.SESSION_ID))
            .leftJoin(USER).on(USER.USER_ID.eq(SESSION.USER_ID))
            .apply {
                if (param.startDate != null && param.endDate != null) {
                    where(DSL.cast(ACTIVITY.CREATED_AT, LocalDate::class.java).between(param.startDate, param.endDate))
                }
            }
            .orderBy(ACTIVITY.CREATED_AT.desc())
            .paginate(page = param.page, limit = param.limit)
            .fetchInto(ActivityResponse::class.java)



}