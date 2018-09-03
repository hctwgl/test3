package com.ald.fanbei.api.web.validator.bean;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component("bankCardBindParam")
public class BankCardBindParam {

    @NotNull
    public String bankNo;

    @NotNull
    public String bankName;

    @NotNull
    public String bankMobile;

    @NotNull
    public String bindNo;

    public String validDate;

    public String safeCode;

}
