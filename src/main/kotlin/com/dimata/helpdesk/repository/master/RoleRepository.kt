package com.dimata.helpdesk.repository.master

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.dto.body.master.RoleBody
import com.dimata.helpdesk.dto.enum.DefaultRole
import com.dimata.helpdesk.dto.response.DropdownResponse
import com.dimata.helpdesk.dto.response.master.RoleResponse
import com.dimata.helpdesk.exception.DataNotFoundException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.gen.Tables.ROLE
import com.dimata.helpdesk.gen.Tables.ROLE_PERMISSION
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL
import java.time.LocalDateTime

@ApplicationScoped
class RoleRepository : Repository() {

    fun create(body: RoleBody): String =
        jooq.newRecord(ROLE, body)
            .also {
                it.roleId = generateTSID()
                it.store()
            }
            .roleId

    fun exists(roleId: String) =
        jooq.fetchExists(ROLE, ROLE.ROLE_ID.eq(roleId), ROLE.DELETED_AT.isNull)

    fun nameExists(roleName: String, roleId: String? = null) =
        jooq.fetchExists(
            ROLE,
            DSL.lower(ROLE.NAME).eq(DSL.lower(roleName))
                .and(when(roleId != null) {
                    true -> ROLE.ROLE_ID.notEqual(roleId)
                    else -> DSL.noCondition()
                })
        )

    fun update(body: RoleBody, roleId: String): String =
        (jooq.fetchOne(ROLE, ROLE.ROLE_ID.eq(roleId).and(ROLE.DELETED_AT.isNull))
            ?: throw DataNotFoundException(ExceptionCode.ROLE_NOT_FOUND))
            .also {
                it.name = body.name
                it.description = body.description
                it.updatedAt = LocalDateTime.now()
                it.store()
            }
            .roleId

    fun dropdownList(name: String?): MutableList<DropdownResponse> =
        jooq.select(ROLE.ROLE_ID.`as`("id"), ROLE.NAME)
            .from(ROLE)
            .where(ROLE.DELETED_AT.isNull)
            .and(ROLE.ROLE_ID.notEqual(DefaultRole.MAINTAINER.id))
            .apply {
                name?.let {
                    and(ROLE.NAME.like("%$name%"))
                }
            }
            .orderBy(ROLE.CREATED_AT.desc())
            .fetchInto(DropdownResponse::class.java)

    fun positionIdExists(positionId: Long) =
        jooq.select(ROLE.ROLE_ID)
            .from(ROLE)
            .where(ROLE.XK_POSITION_ID.eq(positionId))
            .limit(1)
            .fetchOneInto(String::class.java)

    fun getRoles(): MutableList<RoleResponse> =
        jooq.select(
            ROLE.ROLE_ID,
            ROLE.NAME,
            ROLE.DESCRIPTION
        )
            .from(ROLE)
            .where(ROLE.DELETED_AT.isNull)
            .and(ROLE.ROLE_ID.notEqual(DefaultRole.MAINTAINER.id))
            .orderBy(ROLE.CREATED_AT.desc())
            .fetchInto(RoleResponse::class.java)

    fun getById(roleId: String) =
        jooq.select(
            ROLE.ROLE_ID,
            ROLE.NAME,
            ROLE.DESCRIPTION
        )
            .from(ROLE)
            .where(ROLE.DELETED_AT.isNull)
            .and(ROLE.ROLE_ID.eq(roleId))
            .limit(1)
            .fetchOneInto(RoleResponse::class.java)
            ?: throw DataNotFoundException(ExceptionCode.ROLE_NOT_FOUND)

    fun getAssignedPermissions(roleId: String) =
        jooq.select(
            ROLE.ROLE_ID,
            ROLE.NAME,
            DSL.multiset(
                DSL.select(ROLE_PERMISSION.PERMISSION_ID.`as`("id"), ROLE_PERMISSION.permission().NAME.`as`("name"))
                    .from(ROLE_PERMISSION)
                    .where(ROLE_PERMISSION.ROLE_ID.eq(ROLE.ROLE_ID))
            ).convertFrom { it.into(DropdownResponse::class.java) }.`as`("permissions")
        )
            .from(ROLE)
            .where(ROLE.ROLE_ID.eq(roleId))
            .limit(1)
            .fetchOneInto(AssignedRoleResponse::class.java)
            ?: throw DataNotFoundException(ExceptionCode.ROLE_NOT_FOUND)

}