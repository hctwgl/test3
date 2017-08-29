package com.ald.fanbei.api.common.kdniao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class KdniaoReqDataDataTraces {
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date acceptTime;
    private String acceptStation;


    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }
}
