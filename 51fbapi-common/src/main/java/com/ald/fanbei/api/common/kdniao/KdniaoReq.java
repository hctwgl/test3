package com.ald.fanbei.api.common.kdniao;

public class KdniaoReq {
    private String dataSign;
    private int requestType;

    private KdniaoReqData kdniaoReqData;

    public String getDataSign() {
        return dataSign;
    }

    public void setDataSign(String dataSign) {
        this.dataSign = dataSign;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public KdniaoReqData getKdniaoReqData() {
        return kdniaoReqData;
    }

    public void setKdniaoReqData(KdniaoReqData kdniaoReqData) {
        this.kdniaoReqData = kdniaoReqData;
    }
}

