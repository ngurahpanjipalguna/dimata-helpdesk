package com.dimata.helpdesk.core.auth

import com.dimata.helpdesk.dto.enum.TokenType
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.UnauthorizedAccessException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwe
import io.jsonwebtoken.Jwts
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.*
import javax.crypto.spec.SecretKeySpec

@ApplicationScoped
class JwtHandler {

    @ConfigProperty(name = "dimata.aes.key")
    lateinit var aesKey: String

    @ConfigProperty(name = "dimata.access-token.expired")
    lateinit var accessTokenExpired: String

    @ConfigProperty(name = "dimata.refresh-token.expired")
    lateinit var refreshTokenExpired: String

    private val issuer = "www.dimata.com"
    private val audience: String = "PMO"

    val key by lazy {
        val keyByte = Base64.getDecoder().decode(aesKey)
        SecretKeySpec(keyByte, "AES")
    }

    fun encrypt(
        userId: String,
        username: String,
        fullName: String,
        roles: Set<String>?,
        permissions: Set<String>?,
        expiredAt: Date,
        ipAddress: String,
        type: TokenType = TokenType.ACCESS,
        sessionId: String? = null
    ): String =
        Jwts.builder()
            .issuer(issuer)
            .claim("roles", roles)
            .claim("permissions", permissions)
            .claim("type", type.toString())
            .claim("aud", audience)
            .claim("userId", userId)
            .claim("sessionId", sessionId)
            .claim("username", username)
            .claim("fullName", fullName)
            .claim("ipAddress", ipAddress)
            .expiration(expiredAt)
            .encryptWith(key, Jwts.KEY.A256KW, Jwts.ENC.A256GCM)
            .compact()

    fun decrypt(token: String): Jwe<Claims> {
        try {
            return Jwts.parser()
                .decryptWith(key)
                .build()
                .parseEncryptedClaims(token)
        } catch (expired: ExpiredJwtException) {
            throw UnauthorizedAccessException(ExceptionCode.JWT_IS_EXPIRED)
        }
    }

    fun encryptFileCredentials(
        filePath: String,
        fileId: String,
        fileName: String,
        fileType: String,
        extension: String
    ): String =
        Jwts.builder()
            .claim("filePath", filePath)
            .claim("fileId", fileId)
            .claim("fileName", fileName)
            .claim("fileType", fileType)
            .claim("extension", extension)
            .encryptWith(key, Jwts.KEY.A256KW, Jwts.ENC.A256GCM)
            .compact()

}