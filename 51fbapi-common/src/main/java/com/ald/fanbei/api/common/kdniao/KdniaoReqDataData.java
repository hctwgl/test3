package com.ald.fanbei.api.common.kdniao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class KdniaoReqDataData {
    private String eBusinessID;
    private String shipperCode;
    private String logisticCode;
    private boolean success;
    private int state;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date estimatedDeliveryTime;
    private List<KdniaoReqDataDataTraces> traces;

    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getLogisticCode() {
        return logisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        this.logisticCode = logisticCode;
    }


    public Date getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public List<KdniaoReqDataDataTraces> getTraces() {
        return traces;
    }

    public void setTraces(List<KdniaoReqDataDataTraces> traces) {
        this.traces = traces;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
