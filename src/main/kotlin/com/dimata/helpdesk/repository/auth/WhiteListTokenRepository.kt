package com.dimata.helpdesk.repository.auth

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.UnauthorizedAccessException
import com.dimata.helpdesk.gen.Tables.WHITELIST_TOKEN
import com.dimata.helpdesk.gen.enums.WhitelistTokenType
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class WhitelistTokenRepository : Repository() {

    fun create(token: String, type: WhitelistTokenType = WhitelistTokenType.ACCESS): String =
        jooq.newRecord(WHITELIST_TOKEN)
            .also {
                it.id = generateTSID()
                it.token = token
                it.type = type
                it.store()
            }
            .id

    fun delete(token: String) =
        jooq.deleteFrom(WHITELIST_TOKEN)
            .where(WHITELIST_TOKEN.TOKEN.eq(token))
            .execute()

    fun validateToken(token: String) {
        val result = jooq.fetchExists(
            WHITELIST_TOKEN,
            WHITELIST_TOKEN.TOKEN.eq(token)
        )
        if (!result) throw UnauthorizedAccessException(ExceptionCode.TOKEN_NOT_REGISTERED)
    }

}