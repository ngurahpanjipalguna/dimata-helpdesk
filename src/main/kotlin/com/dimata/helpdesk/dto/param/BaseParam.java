package com.dimata.helpdesk.dto.param;

import org.jboss.resteasy.reactive.RestQuery;

public class BaseParam {

    @RestQuery
    private Integer page;
    public Integer getPage() {
        if (page == null) {
            return 0;
        }
        if (page > 0) {
            return page - 1;
        }

        return 0;
    }

    @RestQuery
    public Integer limit = 10;

}