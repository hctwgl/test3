package com.ald.fanbei.api.dal.domain;

public class AfContractPdfDo {
    private Long id;

    private Byte type;

    private Long typeId;

    private String contractPdfUrl;

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