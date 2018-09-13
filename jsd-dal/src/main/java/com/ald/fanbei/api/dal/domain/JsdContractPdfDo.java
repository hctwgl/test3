package com.ald.fanbei.api.dal.domain;

import java.util.Date;

public class JsdContractPdfDo {
    private Long id;

    private Byte type;

    private Long typeId;

    private String contractPdfUrl;

    private String evId;

    private Date gmtCreate;

    private Long userSealId;

    public Long getUserSealId() {
        return userSealId;
    }

    public void setUserSealId(Long userSealId) {
        this.userSealId = userSealId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getEvId() {
        return evId;
    }

    public void setEvId(String evId) {
        this.evId = evId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getContractPdfUrl() {
        return contractPdfUrl;
    }

    public void setContractPdfUrl(String contractPdfUrl) {
        this.contractPdfUrl = contractPdfUrl == null ? null : contractPdfUrl.trim();
    }
}