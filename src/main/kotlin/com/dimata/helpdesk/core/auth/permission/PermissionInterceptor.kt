package com.dimata.helpdesk.core.auth.permission

import com.dimata.helpdesk.core.auth.JwtHandler
import com.dimata.helpdesk.dto.enum.TokenType
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.ForbiddenException
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
class PermissionInterceptor(
    private val jwtHandler: JwtHandler,
    @Context private val resourceInfo: ResourceInfo,
    private val whitelistTokenRepository: WhitelistTokenRepository,
    private val activityRepository: ActivityRepository
) : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {
        val method = resourceInfo.resourceMethod
        val classLevelPermission = resourceInfo.resourceClass.getAnnotation(Permission::class.java)
        val methodLevelPermission = method.getAnnotation(Permission::class.java)
        val permission = methodLevelPermission ?: classLevelPermission
        permission?.let {
            val permissionName = permission.name
            val token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION)
                ?: throw UnauthorizedAccessException(ExceptionCode.UNAUTHORIZED)
            val cleanToken = token.substring("Bearer ".length)
            whitelistTokenRepository.validateToken(cleanToken)
            val claims = jwtHandler.decrypt(cleanToken).payload
            val tokenType = claims.get("type", String::class.java)
            if (tokenType == TokenType.REFRESH.toString()) throw UnauthorizedAccessException(ExceptionCode.TOKEN_NOT_VALID)

            var isPermitted = false
            val permissions = claims["permissions"] as ArrayList<*>
            for (p in permissions) {
                val userPermission = p.toString()
                if (permissionName == userPermission) {
                    isPermitted = true
                    break
                }
            }

            // abaikan permission jika role MAINTAINER
            if (!isPermitted) {
                val roles = claims["roles"] as ArrayList<*>
                for (r in roles) {
                    val userRole = r.toString()
                    if (userRole == "MAINTAINER") {
                        isPermitted = true
                        break
                    }
                }
            }

            if (!isPermitted) throw ForbiddenException(ExceptionCode.FORBIDDEN)

            val username = claims.get("fullName", String::class.java)
            val sessionId = claims.get("sessionId", String::class.java)
            activityRepository.create(permissionName, username, sessionId)
        }
    }

}