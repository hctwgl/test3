package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 用户优惠券上实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2017-11-14 16:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class userReturnBoluomeCouponVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    private String inviteeMobile;
    private String registerTime;
    private String status;
    private String reward;
    public String getInviteeMobile() {
        return inviteeMobile;
    }
    public void setInviteeMobile(String inviteeMobile) {
        this.inviteeMobile = inviteeMobile;
    }
    public String getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getReward() {
        return reward;
    }
    public void setReward(String reward) {
        this.reward = reward;
    }
    
}