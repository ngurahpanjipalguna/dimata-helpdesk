package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.core.auth.logged.Logged
import com.dimata.helpdesk.core.auth.permission.Permission
import com.dimata.helpdesk.dto.body.master.PermissionBody
import com.dimata.helpdesk.dto.custom.auth.UserPrincipal
import com.dimata.helpdesk.dto.response.BaseResponse
import com.dimata.helpdesk.dto.response.master.PermissionsResponse
import com.dimata.helpdesk.exception.DataAlreadyExistException
import com.dimata.helpdesk.exception.ExceptionCode
import com.dimata.helpdesk.exception.ForbiddenException
import com.dimata.helpdesk.exception.FormatException
import com.dimata.helpdesk.repository.master.PermissionRepository
import jakarta.transaction.Transactional
import jakarta.validation.Validator
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery

@Path("/api/v1/master/permission")
class PermissionController(
    private val permissionRepository: PermissionRepository,
    private val validator: Validator
) {

    @Transactional
    @POST
    @Logged
    fun create(body: PermissionBody, @Context principal: UserPrincipal): BaseResponse {
        if (!principal.roles.contains("MAINTAINER"))
            throw ForbiddenException(ExceptionCode.FORBIDDEN)

        FormatException.validateObject(body, validator)

        if (permissionRepository.nameExists(body.name!!)) {
            throw DataAlreadyExistException(ExceptionCode.PERMISSION_NAME_ALREADY_EXISTS)
        }
        val permissionId = permissionRepository.create(body)
        return BaseResponse(
            message = "Permission telah ditambahkan",
            data = permissionId
        )
    }

    @GET
    @Permission("view_permission")
    @Path("/dropdown")
    fun getDropdown(@RestQuery name: String? = null) =
        permissionRepository.dropdownList(name)

    @GET
    @Logged
    fun getPermissions(@Context principal: UserPrincipal) =
        kotlin.run {
            if (!principal.roles.contains("MAINTAINER")) {
                throw ForbiddenException(ExceptionCode.FORBIDDEN)
            }
            permissionRepository.getPermissions()
        }

    @PUT
    @Logged
    @Path("/{permissionId}")
    @Transactional
    fun update(@RestPath permissionId: String, body: PermissionBody, @Context principal: UserPrincipal): BaseResponse {
        if (!principal.roles.contains("MAINTAINER"))
            throw ForbiddenException(ExceptionCode.FORBIDDEN)

        FormatException.validateObject(body, validator)
        if (permissionRepository.nameExists(body.name!!)) {
            throw DataAlreadyExistException(ExceptionCode.PERMISSION_NAME_ALREADY_EXISTS)
        }
        permissionRepository.update(body, permissionId)
        return BaseResponse(
            message = "Permission telah diubah",
            data = permissionId
        )
    }

    @GET
    @Path("/{permissionId}")
    @Logged
    fun getById(@RestPath permissionId: String, @Context principal: UserPrincipal): PermissionsResponse {
        if (!principal.roles.contains("MAINTAINER"))
            throw ForbiddenException(ExceptionCode.FORBIDDEN)
        return permissionRepository.getById(permissionId)
    }

}