package com.dimata.helpdesk.core.auth.logged

import com.dimata.helpdesk.core.auth.JwtHandler
import com.dimata.helpdesk.dto.enum.TokenType
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.UnauthorizedAccessException
import com.dimata.helpdesk.repository.auth.ActivityRepository
import com.dimata.helpdesk.repository.auth.WhitelistTokenRepository
import jakarta.annotation.Priority
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ResourceInfo
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.ext.Provider

@Provider
@Priority(Priorities.AUTHORIZATION)
class LoggedInterceptor(
    private val jwtHandler: JwtHandler,
    @Context private val resourceInfo: ResourceInfo,
    private val whitelistTokenRepository: WhitelistTokenRepository,
    private val activityRepository: ActivityRepository
) : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {
        val method = resourceInfo.resourceMethod
        val classLevel = resourceInfo.resourceClass.getAnnotation(Logged::class.java)
        val methodLevel = method.getAnnotation(Logged::class.java)
        val logged = methodLevel ?: classLevel
        logged?.let {
            val event = logged.event
            val token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)
                ?: throw UnauthorizedAccessException(ExceptionCode.UNAUTHORIZED)
            val cleanToken = token.substring("Bearer ".length)
            whitelistTokenRepository.validateToken(cleanToken)
            val claims = jwtHandler.decrypt(cleanToken).payload
            val tokenType = claims.get("type", String::class.java)
            if (tokenType == TokenType.REFRESH.toString()) throw UnauthorizedAccessException(ExceptionCode.TOKEN_NOT_VALID)

            if (event != "") {
                val username = claims.get("fullName", String::class.java)
                val sessionId = claims.get("sessionId", String::class.java)
                activityRepository.create(event, username, sessionId)
            }
        }
    }

}