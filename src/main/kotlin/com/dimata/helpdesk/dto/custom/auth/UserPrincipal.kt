package com.dimata.helpdesk.dto.custom.auth

import com.dimata.helpdesk.core.auth.JwtHandler
import com.dimata.helpdesk.dto.enum.TokenType
import com.dimata.helpdesk.exception.FormatException
import io.jsonwebtoken.Claims
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.HttpHeaders
import java.util.stream.Collectors

@RequestScoped
class UserPrincipal(
    @Context private val headers: HttpHeaders,
    jwtHandler: JwtHandler
) {

    var tokenType: TokenType
    var userId: String
    var username: String
    var fullName: String
    var sessionId: String
    var permissions: Set<String>
    var roles: Set<String>
    var ipAddress: String
    var token: String

    init {
        val header = headers.getHeaderString(HttpHeaders.AUTHORIZATION)
            ?: throw FormatException("Kesalahan internal, tidak bisa mengambil principal jika tidak diberi anotasi @Logged")
        this.token = header.substring("Bearer".length).trim()
        val claims = jwtHandler.decrypt(token).payload
        this.tokenType = TokenType.valueOf(claims.get("type", String::class.java))
        this.userId = claims.get("userId", String::class.java)
        this.username = claims.get("username", String::class.java)
        this.fullName = claims.get("fullName", String::class.java)
        this.sessionId = claims.get("sessionId", String::class.java)
        this.permissions = unwrapPermission(claims)
        this.roles = unwrapRoles(claims)
        this.ipAddress = claims.get("ipAddress", String::class.java)
    }

    private fun unwrapRoles(claims: Claims): Set<String> {
        val roles = HashSet<String>()
        val rolesObject = claims["roles"]
        if (rolesObject is List<*>) {
            val temp = rolesObject as List<*>
            val tempAsSet = temp
                .stream()
                .filter { obj: Any? -> String::class.java.isInstance(obj) }
                .map { obj: Any? -> String::class.java.cast(obj) }
                .collect(Collectors.toSet())
            roles.addAll(tempAsSet)
        }

        return roles
    }

    private fun unwrapPermission(claims: Claims): Set<String> {
        val permission = HashSet<String>()
        val permissionObject = claims["permissions"]
        if (permissionObject is List<*>) {
            val temp = permissionObject as List<*>
            val tempAsSet = temp
                .stream()
                .filter { obj: Any? -> String::class.java.isInstance(obj) }
                .map { obj: Any? -> String::class.java.cast(obj) }
                .collect(Collectors.toSet())
            permission.addAll(tempAsSet)
        }

        return permission
    }
}