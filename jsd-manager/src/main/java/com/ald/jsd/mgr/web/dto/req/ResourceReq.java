package com.ald.jsd.mgr.web.dto.req;

import javax.validation.constraints.NotNull;

public class ResourceReq {
    @NotNull
    public String pattern;

    public String loanAmount;

    public Long id;
}
