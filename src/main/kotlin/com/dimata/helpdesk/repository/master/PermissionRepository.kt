package com.dimata.helpdesk.repository.master

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.dto.body.master.PermissionBody
import com.dimata.helpdesk.dto.response.DropdownResponse
import com.dimata.helpdesk.dto.response.master.PermissionsResponse
import com.dimata.helpdesk.exception.DataNotFoundException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.gen.Tables.PERMISSION
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL

@ApplicationScoped
class PermissionRepository : Repository() {

    fun create(body: PermissionBody): String =
        jooq.newRecord(PERMISSION, body)
            .also {
                it.permissionId = generateTSID()
                it.name = format(body.name!!)
                it.store()
            }
            .permissionId

    fun nameExists(name: String) =
        jooq.fetchExists(
            PERMISSION,
            DSL.lower(PERMISSION.NAME).eq(DSL.lower(name))
        )

    fun dropdownList(name: String?): MutableList<DropdownResponse> =
        jooq.select(
            PERMISSION.PERMISSION_ID.`as`("id"),
            PERMISSION.NAME
        )
            .from(PERMISSION)
            .where(PERMISSION.DELETED_AT.isNull)
            .apply {
                name?.let {
                    and(PERMISSION.NAME.like("%$name%"))
                }
            }
            .fetchInto(DropdownResponse::class.java)

    fun getPermissions(): MutableList<PermissionsResponse> =
        jooq.select(
            PERMISSION.PERMISSION_ID,
            PERMISSION.NAME,
            PERMISSION.DESCRIPTION
        )
            .from(PERMISSION)
            .where(PERMISSION.DELETED_AT.isNull)
            .orderBy(PERMISSION.DELETED_AT.desc())
            .fetchInto(PermissionsResponse::class.java)

    fun update(body: PermissionBody, permissionId: String) =
        (jooq.fetchOne(PERMISSION, PERMISSION.PERMISSION_ID.eq(permissionId))
            ?: throw DataNotFoundException(ExceptionCode.PERMISSION_NOT_FOUND))
            .also {
                it.name = format(body.name!!)
                it.description = body.description
                it.store()
            }

    fun getById(permissionId: String) =
        jooq.select(
            PERMISSION.PERMISSION_ID,
            PERMISSION.NAME,
            PERMISSION.DESCRIPTION
        )
            .from(PERMISSION)
            .where(PERMISSION.PERMISSION_ID.eq(permissionId))
            .and(PERMISSION.DELETED_AT.isNull)
            .limit(1)
            .fetchOneInto(PermissionsResponse::class.java)
            ?: throw DataNotFoundException(ExceptionCode.PERMISSION_NOT_FOUND)

    private fun format(name: String) =
        name.trim().lowercase().replace("\\s+".toRegex(), "_")

}