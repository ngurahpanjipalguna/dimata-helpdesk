package com.dimata.helpdesk.repository.auth

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.exception.DataNotFoundException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.gen.Tables.SESSION
import com.dimata.helpdesk.gen.enums.SessionState
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL
import java.time.LocalDateTime

@ApplicationScoped
class SessionRepository : Repository() {

    fun create(
        userId: String,
        refreshToken: String,
        ipAddress: String?,
        expiredAt: LocalDateTime,
        state: SessionState = SessionState.ACTIVE
    ): String =
        jooq.newRecord(SESSION)
            .also {
                it.sessionId = generateTSID()
                it.userId = userId
                it.state = state
                it.refreshToken = refreshToken
                it.ipAddress = ipAddress
                it.createdAt = LocalDateTime.now()
                it.expiredAt = expiredAt
                it.store()
            }
            .sessionId

    fun countUserSession(userId: String) =
        jooq.fetchCount(
            SESSION, SESSION.USER_ID.eq(userId),
            SESSION.STATE.eq(SessionState.ACTIVE)
        )

    fun turnOffFirstSession(userId: String) =
        jooq.update(SESSION)
            .set(SESSION.STATE, SessionState.LOGOUT)
            .where(SESSION.USER_ID.eq(userId))
            .and(SESSION.STATE.eq(SessionState.ACTIVE))
            .orderBy(SESSION.CREATED_AT.asc())
            .limit(1)
            .execute()

    fun logoutSession(userId: String, ipAddress: String) =
        jooq.update(SESSION)
            .set(SESSION.STATE, SessionState.LOGOUT)
            .where(SESSION.USER_ID.eq(userId))
            .and(SESSION.IP_ADDRESS.eq(ipAddress))
            .execute()

    fun getByRefreshToken(refreshToken: String) =
        jooq.fetchOne(
            SESSION,
            SESSION.REFRESH_TOKEN.eq(refreshToken),
            SESSION.STATE.eq(SessionState.ACTIVE),
            SESSION.EXPIRED_AT.greaterThan(DSL.currentLocalDateTime())
        ) ?: throw DataNotFoundException(ExceptionCode.SESSION_NOT_FOUND)

}