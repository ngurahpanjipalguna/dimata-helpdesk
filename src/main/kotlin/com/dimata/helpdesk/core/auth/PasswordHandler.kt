package com.dimata.helpdesk.core.auth

import io.quarkus.elytron.security.common.BcryptUtil

fun generateBcrypt(password : String) : String {
    return BcryptUtil.bcryptHash(password)
}

fun passwordMatch(plainPassword: String, hashedPassword: String): Boolean {
    val hashedPasswordSplitted = hashedPassword.replace(Regex("\\{bcrypt\\}"), "")
    return BcryptUtil.matches(plainPassword, hashedPasswordSplitted)
}

fun comparePassword(password: String, hashedPassword : String) : Boolean{
    return (generateBcrypt(password) == hashedPassword)
}