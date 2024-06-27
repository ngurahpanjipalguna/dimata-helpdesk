package com.dimata.helpdesk.controller.auth

import com.dimata.helpdesk.core.auth.JwtHandler
import com.dimata.helpdesk.core.auth.logged.Logged
import com.dimata.helpdesk.core.auth.passwordMatch
import com.dimata.helpdesk.dto.body.auth.RefreshTokenLoginBody
import com.dimata.helpdesk.dto.body.auth.UsernameLoginBody
import com.dimata.helpdesk.dto.custom.auth.SessionIdAndRefreshTokenOut
import com.dimata.helpdesk.dto.custom.auth.UserAndRolesOut
import com.dimata.helpdesk.dto.custom.auth.UserPrincipal
import com.dimata.helpdesk.dto.enum.TokenType
import com.dimata.helpdesk.dto.response.BaseResponse
import com.dimata.helpdesk.dto.response.auth.LoginResponse
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.ForbiddenException
import com.dimata.helpdesk.exception.FormatException
import com.dimata.helpdesk.repository.auth.ActivityRepository
import com.dimata.helpdesk.repository.auth.SessionRepository
import com.dimata.helpdesk.repository.auth.WhitelistTokenRepository
import com.dimata.helpdesk.repository.master.RolePermissionRepository
import com.dimata.helpdesk.repository.master.UserRepository
import jakarta.transaction.Transactional
import jakarta.validation.Validator
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import java.time.ZoneId
import java.util.*

@Path("/api/v1/auth")
class AuthController(
    private val jwtHandler: JwtHandler,
    private val userRepository: UserRepository,
    private val validator: Validator,
    private val sessionRepository: SessionRepository,
    private val whitelistTokenRepository: WhitelistTokenRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val activityRepository: ActivityRepository
) {

    @Path("/username-login")
    @POST
    @Transactional
    fun usernameLogin(body: UsernameLoginBody): LoginResponse {
        FormatException.validateObject(body, validator)
        val user = userRepository.getByUsername(body.username!!)
        val roleIds = user.roles?.map { r -> r.roleId }?.toSet()
        val roleNames = user.roles?.map { r -> r.name }?.toSet()

        // cek apakah batas login tercapai, jika ya logout session pertama
        checkLoginAttempt(user)

        // cek apakah password sesuai
        if (!passwordMatch(body.password!!, user.password)) {
            throw ForbiddenException(ExceptionCode.PASSWORD_NOT_MATCH)
        }

        // ambil permissions
        val permissions = rolePermissionRepository.getPermissions(roleIds)
        val permissionNames = permissions.map { r -> r.name }.toSet()

        // buat session baru
        val session = createSession(user, body.ipAddress!!, roleNames, permissionNames)

        // buat access token dan daftarkan ke whitelist
        val accessToken = createAccessToken(user, session.sessionId, body.ipAddress, roleNames, permissionNames)

        // catat aktivitas
        activityRepository.create("login", user.fullName, session.sessionId)

        return LoginResponse(
            session.sessionId,
            user.userId,
            user.username,
            user.fullName,
            roleNames,
            permissionNames,
            session.expiredAt,
            accessToken = accessToken,
            refreshToken = session.refreshToken
        )
    }

    @Path("/refresh-token-login")
    @POST
    @Transactional
    fun refreshTokenLogin(body: RefreshTokenLoginBody): LoginResponse {
        FormatException.validateObject(body, validator)
        val session = sessionRepository.getByRefreshToken(body.refreshToken!!)
        if (session.ipAddress != body.ipAddress) {
            session.ipAddress = body.ipAddress
            session.store()
        }

        val user = userRepository.getByUserId(session.userId)
        val roleIds = user.roles?.map { r -> r.roleId }?.toSet()
        val roleNames = user.roles?.map { r -> r.name }?.toSet()

        // ambil permissions
        val permissions = rolePermissionRepository.getPermissions(roleIds)
        val permissionNames = permissions.map { it.name }.toSet()
        // buat access token dan daftarkan ke whitelist
        val accessToken = createAccessToken(user, session.sessionId,  body.ipAddress!!, roleNames, permissionNames)

        return LoginResponse(
            session.sessionId,
            user.userId,
            user.username,
            user.fullName,
            roleNames,
            permissionNames,
            session.expiredAt,
            accessToken = accessToken,
            refreshToken = session.refreshToken
        )
    }

    @Path("/logout")
    @Transactional
    @PATCH
    @Logged("logout")
    fun logout(@Context principal: UserPrincipal): BaseResponse {
        sessionRepository.logoutSession(principal.userId, principal.ipAddress)
        whitelistTokenRepository.delete(principal.token)
        return BaseResponse(
            message = "Logout berhasil!",
            data = null
        )
    }

    private fun createSession(user: UserAndRolesOut, ipAddress: String, roles: Set<String>?, permissions: Set<String>?): SessionIdAndRefreshTokenOut {
        val expiredInMills = jwtHandler.refreshTokenExpired.toLong()
        val expiredAt = Date(Date().time + expiredInMills)
        val expiredInLocal = expiredAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val refreshToken = jwtHandler.encrypt(
            user.userId,
            user.username,
            user.fullName,
            roles,
            permissions,
            expiredAt,
            ipAddress,
            type = TokenType.REFRESH
        )
        val sessionId = sessionRepository.create(
            user.userId,
            refreshToken,
            ipAddress,
            expiredInLocal
        )

        return SessionIdAndRefreshTokenOut(sessionId, refreshToken, expiredInLocal)
    }

    private fun createAccessToken(user: UserAndRolesOut, sessionId: String, ipAddress: String, roles: Set<String>?,
                                  permissions: Set<String>?): String {
        val expiredInMills = jwtHandler.accessTokenExpired.toLong()
        val expiredAt = Date(Date().time + expiredInMills)
        val accessToken = jwtHandler.encrypt(
            user.userId,
            user.username,
            user.fullName,
            roles,
            permissions,
            expiredAt,
            ipAddress,
            sessionId = sessionId
        )
        whitelistTokenRepository.create(accessToken)

        return accessToken
    }

    private fun checkLoginAttempt(user: UserAndRolesOut) {
        val activeSession = sessionRepository.countUserSession(user.userId)
        val allowedActiveSession = user.maxLoginAttempt
        if (activeSession == allowedActiveSession) {
            sessionRepository.turnOffFirstSession(user.userId)
        }
    }

}