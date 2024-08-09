package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.core.auth.permission.Permission
import com.dimata.helpdesk.dto.body.master.RoleBody
import com.dimata.helpdesk.dto.body.master.RolePermissionBody
import com.dimata.helpdesk.dto.body.master.AssignPermissionOut
import com.dimata.helpdesk.dto.response.BaseResponse
import com.dimata.helpdesk.dto.response.master.CreateRoleResponse
import com.dimata.helpdesk.exception.DataAlreadyExistException
import com.dimata.helpdesk.exception.DataNotFoundException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.FormatException
import com.dimata.helpdesk.repository.master.RolePermissionRepository
import com.dimata.helpdesk.repository.master.RoleRepository
import jakarta.transaction.Transactional
import jakarta.validation.Validator
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery

@Path("/api/v1/master/role")
class RoleController(
    private val roleRepository: RoleRepository,
    private val validator: Validator,
    private val rolePermissionRepository: RolePermissionRepository
) {

    @Transactional
    @POST
    @Permission("create_role")
    fun create(body: RoleBody): BaseResponse {
        FormatException.validateObject(body, validator)
        if (roleRepository.nameExists(body.name!!)) {
            throw DataAlreadyExistException(ExceptionCode.ROLE_NAME_EXISTS)
        }
        val roleId = roleRepository.create(body)
        val assignedPermission = assignPermissions(roleId, body.permissionsId)
        return BaseResponse(
            message = "Role telah ditambahkan",
            data = CreateRoleResponse(roleId, assignedPermission)
        )
    }

    @Transactional
    @POST
    @Permission("create_role")
    @Path("/assign-permission")
    fun assignPermission(body: RolePermissionBody): BaseResponse {
        FormatException.validateObject(body, validator)
        if (!roleRepository.exists(body.roleId!!)) {
            throw DataNotFoundException(ExceptionCode.ROLE_NOT_FOUND)
        }
        val result = assignPermissions(body.roleId, body.permissionsId)
        return BaseResponse(
            message = "Permission telah di tambahkan",
            data = result
        )
    }

    @GET
    @Permission("view_role")
    @Path("/dropdown")
    fun getDropdown(@RestQuery name: String?) =
        roleRepository.dropdownList(name)

    @GET
    @Permission("view_role")
    fun getRoles() =
        roleRepository.getRoles()

    @GET
    @Permission("view_role")
    @Path("/{roleId}")
    fun getById(@RestPath roleId: String) =
        roleRepository.getById(roleId)

    @PUT
    @Permission("update_role")
    @Path("/{roleId}")
    @Transactional
    fun update(body: RoleBody, @RestPath roleId: String): BaseResponse {
        FormatException.validateObject(body, validator)
        if (roleRepository.nameExists(body.name!!, roleId)) {
            throw DataAlreadyExistException(ExceptionCode.ROLE_NAME_EXISTS)
        }

        roleRepository.update(body, roleId)
        return BaseResponse(
            message = "Role berhasil di update",
            data = roleId
        )
    }

    @GET
    @Permission("view_role")
    @Path("/get-assigned-permission/{roleId}")
    fun getAssignedPermission(@RestPath roleId: String) =
        roleRepository.getAssignedPermissions(roleId)

    private fun assignPermissions(roleId: String, permissionsId: Set<String>?): AssignPermissionOut? {
        permissionsId?.let {
            val success = HashSet<String>()
            val fail = HashSet<String>()
            permissionsId.forEach {  permissionId ->
                val result = rolePermissionRepository.assign(roleId, permissionId)
                when(result) {
                    null -> fail.add(permissionId)
                    else -> success.add(permissionId)
                }
            }

            return AssignPermissionOut(success, fail)
        }

        return null
    }

}