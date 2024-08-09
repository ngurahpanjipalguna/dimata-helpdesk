package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.BannerBody
import com.dimata.helpdesk.StatusBody
import com.dimata.helpdesk.repository.BannerRepository
import com.dimata.helpdesk.response.BaseResponse
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath
import com.dimata.helpdesk.core.auth.permission.Permission
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.*


@Path("/api/v1/master")
class BannerController(
    private val bannerRepository: BannerRepository
) {

    @GET
    @Path("/banner")
    @Permission("view_banner")
    fun getAll(@QueryParam("page") page: Int?, @QueryParam("limit") limit: Int?): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val contacts = bannerRepository.getAll(pageValue, limitValue)
        return Response.ok(contacts).build()
    }

    @GET
    @Path("/banner/{id}")
    @Permission("view_banner")
    fun getById(@RestPath id: String): Response {
        return try {
            val banner = bannerRepository.getById(id)
            Response.ok(banner).build()
        } catch (e: NotFoundException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(BaseResponse(message = "Data gak ketemu nich", data = null))
                .build()
        }

    }

    @POST
    @Transactional
    @Path("/banner")
    @Permission("create_banner")
    fun create(@Valid body: BannerBody): BaseResponse {
        val id = bannerRepository.create(body)
        return BaseResponse(
            message = "Banner created",
            data = id
        )
    }


    @PUT
    @Transactional
    @Path("/banner/{id}")
    @Permission("update_banner")
    fun update(@RestPath id: String, @Valid body: BannerBody): BaseResponse {
        bannerRepository.update(body, id)
        return BaseResponse(
            message = "Banner updated",
            data = id
        )
    }

    @DELETE
    @Path("/banner/{id}")
    @Transactional
    @Permission("delete_banner")
    fun delete(@RestPath id: String): BaseResponse {
        bannerRepository.softDelete(id)
        return BaseResponse(
            message = "Banner deleted",
            data = null
        )
    }
}