package com.dimata.helpdesk.dto.param;

import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;

public class ActivityParam extends BaseParam {

    @RestQuery
    public LocalDate startDate;

    @RestQuery
    public LocalDate endDate;

}