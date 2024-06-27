package com.dimata.helpdesk.controller.master.contact

import com.dimata.helpdesk.repository.BenefitRepository
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response

@Path("/api/v1/master")
class BenefitController(
    private val benefitRepository: BenefitRepository
) {

    @GET
    @Path("/benefit")
    fun getAll(@QueryParam("page") page: Int?, @QueryParam("limit") limit: Int?): Response {
        val pageValue = page ?: 1 // Default to page 1 if not provided
        val limitValue = limit ?: 10 // Default to limit 10 if not provided
        val contacts = benefitRepository.getAll(pageValue, limitValue)
        return Response.ok(contacts).build()
    }
}