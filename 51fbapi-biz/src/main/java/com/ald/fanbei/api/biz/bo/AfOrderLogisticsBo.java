package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataDataTraces;

import java.util.List;

public class AfOrderLogisticsBo extends AbstractSerial {
    private static final long serialVersionUID = 483153876542142380L;
    private String stateDesc;
    private String shipperName;
    private String shipperCode;
    private KdniaoReqDataDataTraces newestInfo;
    private List<KdniaoReqDataDataTraces> tracesInfo;
    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public KdniaoReqDataDataTraces getNewestInfo() {
        return newestInfo;
    }

    public void setNewestInfo(KdniaoReqDataDataTraces newestInfo) {
        this.newestInfo = newestInfo;
    }

    public List<KdniaoReqDataDataTraces> getTracesInfo() {
        return tracesInfo;
    }

    public void setTracesInfo(List<KdniaoReqDataDataTraces> tracesInfo) {
        this.tracesInfo = tracesInfo;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }
}
