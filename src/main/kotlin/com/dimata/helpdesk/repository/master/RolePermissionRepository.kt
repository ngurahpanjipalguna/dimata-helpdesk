package com.dimata.helpdesk.repository.master

import com.dimata.helpdesk.core.auth.generateTSID
import com.dimata.helpdesk.dto.custom.auth.PermissionOut
import com.dimata.helpdesk.gen.Tables.PERMISSION
import com.dimata.helpdesk.gen.Tables.ROLE_PERMISSION
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL

@ApplicationScoped
class RolePermissionRepository : Repository() {

    fun getPermissions(roleIds: Set<String>?): List<PermissionOut> =
        jooq.select(ROLE_PERMISSION.permission().PERMISSION_ID, ROLE_PERMISSION.permission().NAME)
            .from(ROLE_PERMISSION)
            .where(ROLE_PERMISSION.ROLE_ID.`in`(roleIds))
            .and(ROLE_PERMISSION.permission().DELETED_AT.isNull)
            .fetchInto(PermissionOut::class.java)

    fun assign(roleId: String, permissionId: String) =
        when(jooq.fetchExists(PERMISSION, PERMISSION.PERMISSION_ID.eq(permissionId), PERMISSION.DELETED_AT.isNull)) {
            true -> {
                jooq.insertInto(ROLE_PERMISSION, ROLE_PERMISSION.ROLE_PERMISSION_ID, ROLE_PERMISSION.ROLE_ID, ROLE_PERMISSION.PERMISSION_ID)
                    .select(
                        DSL.select(
                            DSL.inline(generateTSID()), // generateTSID() adalah fungsi Anda untuk menghasilkan ID baru
                            DSL.inline(roleId),
                            DSL.inline(permissionId)
                        )
                            .whereNotExists(
                                DSL.selectOne()
                                    .from(ROLE_PERMISSION)
                                    .where(ROLE_PERMISSION.ROLE_ID.eq(roleId)
                                        .and(ROLE_PERMISSION.PERMISSION_ID.eq(permissionId)))
                            )
                    )
                    .execute()
                roleId
            }
            false -> null
        }

}