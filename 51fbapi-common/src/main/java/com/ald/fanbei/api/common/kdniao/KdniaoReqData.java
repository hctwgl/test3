package com.ald.fanbei.api.common.kdniao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class KdniaoReqData {
    private String eBusinessID;
    private int count;
    private Date pushTime;
    private List<KdniaoReqDataData> data;

    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public Date getPushTime() {
        return pushTime;
    }

    public void setPushTime(Date pushTime) {
        this.pushTime = pushTime;
    }

    public List<KdniaoReqDataData> getData() {
        return data;
    }

    public void setData(List<KdniaoReqDataData> data) {
        this.data = data;
    }
}
