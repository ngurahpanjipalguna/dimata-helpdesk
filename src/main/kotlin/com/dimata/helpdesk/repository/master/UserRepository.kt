package com.dimata.helpdesk.repository.master

import com.dimata.helpdesk.core.auth.generateBcrypt
import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.dto.body.master.CreateUserBody
import com.dimata.helpdesk.dto.body.master.UpdateUserBody
import com.dimata.helpdesk.dto.body.notification.WelcomeEmailBody
import com.dimata.helpdesk.dto.custom.auth.RoleOut
import com.dimata.helpdesk.dto.custom.auth.UserAndRolesOut
import com.dimata.helpdesk.dto.custom.master.EmailUsernameOut
import com.dimata.helpdesk.dto.enum.DefaultUser
import com.dimata.helpdesk.response.DropdownResponse
import com.dimata.helpdesk.dto.response.master.UserDetailResponse
import com.dimata.helpdesk.dto.response.master.UserListResponse
import com.dimata.helpdesk.dto.response.master.UserMeResponse
import com.dimata.helpdesk.exception.DataNotFoundException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.gen.Tables.ROLE_USER
import com.dimata.helpdesk.gen.Tables.USER
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL
import java.time.LocalDateTime

@ApplicationScoped
class UserRepository : Repository() {

    fun create(body: CreateUserBody): String =
        jooq.newRecord(USER, body)
            .also {
                it.userId = generateTSID()
                it.password = "{bcrypt}${generateBcrypt(body.password!!)}"
                it.createdAt = LocalDateTime.now()
                it.updatedAt = LocalDateTime.now()
                it.store()
            }
            .userId

    fun update(body: UpdateUserBody, userId: String): String =
        (jooq.fetchOne(USER, USER.USER_ID.eq(userId))
            ?: throw DataNotFoundException(ExceptionCode.USER_NOT_FOUND))
            .also {
                it.username = body.username
                it.fullName = body.fullName
                body.maxLoginAttempt?.let { maxLogin ->
                    it.maxLoginAttempt = maxLogin
                }
                it.maxLoginAttempt
                it.email = body.email
                it.updatedAt = LocalDateTime.now()
                it.store()
            }
            .userId

    fun getById(userId: String) =
        jooq.select(
            USER.USER_ID,
            USER.USERNAME,
            USER.FULL_NAME,
            USER.EMAIL,
            USER.MAX_LOGIN_ATTEMPT
        )
            .from(USER)
            .where(USER.USER_ID.eq(userId))
            .limit(1)
            .fetchOneInto(UserDetailResponse::class.java)

    fun usernameEmailExists(email: String? = null, username: String, userId: String? = null) =
        jooq.select(USER.USERNAME, USER.EMAIL)
            .from(USER)
            .where(USER.USERNAME.eq(username))
            .apply {
                email?.let { or(USER.EMAIL.eq(email)) }
            }
            .and(
                when(userId == null) {
                    true -> DSL.noCondition()
                    else -> USER.USER_ID.notEqual(userId)
                }
            )
            .limit(1)
            .fetchOneInto(EmailUsernameOut::class.java)

    fun getByUsername(username: String) =
        selectFromUser()
            .where(USER.USERNAME.eq(username))
            .and(USER.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(UserAndRolesOut::class.java)
            ?: throw DataNotFoundException(ExceptionCode.USERNAME_NOT_FOUND)

    fun getByUserId(userId: String) =
        selectFromUser()
            .where(USER.USER_ID.eq(userId))
            .and(USER.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(UserAndRolesOut::class.java)
            ?: throw DataNotFoundException(ExceptionCode.USER_NOT_FOUND)

    fun userList(): MutableList<UserListResponse> =
        jooq.select(
            USER.USER_ID,
            USER.USERNAME,
            USER.FULL_NAME,
            USER.MAX_LOGIN_ATTEMPT,
            DSL.multiset(
                DSL.select(ROLE_USER.role().NAME).from(ROLE_USER).where(ROLE_USER.USER_ID.eq(USER.USER_ID))
            ).convertFrom {
                it.into(String::class.java).toSet()
            }.`as`("roles")
        )
            .from(USER)
            .where(USER.DELETED_AT.isNull)
            .and(USER.USER_ID.notEqual(DefaultUser.MAINTAINER.id))
            .fetchInto(UserListResponse::class.java)

    fun me(userId: String) =
        jooq.select(
            USER.USER_ID,
            USER.USERNAME,
            USER.FULL_NAME,
            USER.EMAIL,
            USER.CREATED_AT,
            USER.UPDATED_AT,
            DSL.multiset(
                DSL.select(ROLE_USER.role().NAME).from(ROLE_USER).where(ROLE_USER.USER_ID.eq(USER.USER_ID))
            ).convertFrom {
                it.into(String::class.java).toSet()
            }.`as`("roles")
        )
            .from(USER)
            .where(USER.USER_ID.eq(userId))
            .and(USER.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(UserMeResponse::class.java)
            ?: throw DataNotFoundException(ExceptionCode.USER_NOT_FOUND)

    fun exists(userId: String) =
        jooq.fetchExists(USER, USER.USER_ID.eq(userId), USER.DELETED_AT.isNull)

    fun dropdownList(name: String?): MutableList<DropdownResponse> =
        jooq.select(
            USER.USER_ID.`as`("id"),
            USER.FULL_NAME.`as`("name")
        )
            .from(USER)
            .where(USER.DELETED_AT.isNull)
            .apply {
                name?.let {
                    and(USER.FULL_NAME.like("%$name%"))
                }
            }
            .fetchInto(DropdownResponse::class.java)

    fun employeeIdExists(employeeId: Long) =
        jooq.fetchExists(USER, USER.XK_EMPLOYEE_ID.eq(employeeId))

    fun getCredentialForWelcomeEmail(userId: String) =
        jooq.select(
            USER.FULL_NAME,
            USER.USERNAME,
            USER.EMAIL,
            DSL.multiset(
                DSL.select(ROLE_USER.role().NAME)
                    .from(ROLE_USER)
                    .where(ROLE_USER.USER_ID.eq(USER.USER_ID))
            ).convertFrom {
                it.into(String::class.java).toSet().joinToString(", ")
            }.`as`("roles")
        )
            .from(USER)
            .where(USER.USER_ID.eq(userId))
            .limit(1)
            .fetchOneInto(WelcomeEmailBody::class.java)

    private fun selectFromUser() =
        jooq.select(
            USER.USER_ID,
            USER.USERNAME,
            USER.PASSWORD,
            USER.FULL_NAME,
            USER.MAX_LOGIN_ATTEMPT,
            DSL.multiset(
                DSL.select(ROLE_USER.role().NAME.`as`("name"), ROLE_USER.role().ROLE_ID.`as`("roleId"))
                    .from(ROLE_USER)
                    .where(ROLE_USER.USER_ID.eq(USER.USER_ID))
            ).convertFrom {
                it.into(RoleOut::class.java).toSet()
            }.`as`("roles")
        )
            .from(USER)

}