package com.ald.fanbei.api.common.kdniao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 快递鸟返回值信息
 */
public class KdniaoRes {
    private String eBusinessID;
    private Date updateTime;
    private boolean success;
    private String reason;

    public String geteBusinessID() {
        return eBusinessID;
    }

    public void seteBusinessID(String eBusinessID) {
        this.eBusinessID = eBusinessID;
    }
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
